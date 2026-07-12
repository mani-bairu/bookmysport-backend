package com.bookmysport.backend.payment.service;

import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.booking.repository.BookingRepository;
import com.bookmysport.backend.booking.service.BookingService;
import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.payment.dto.Response.PaymentResponseDto;
import com.bookmysport.backend.payment.dto.request.VerifyPaymentRequestDto;
import com.bookmysport.backend.payment.entity.PaymentEntity;
import com.bookmysport.backend.payment.enums.PaymentGateway;
import com.bookmysport.backend.payment.enums.PaymentStatus;
import com.bookmysport.backend.payment.repository.PaymentRepository;
import com.bookmysport.backend.slot.service.SlotLockService;
import com.bookmysport.backend.slot.service.SlotService;
import com.razorpay.Order;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

import static com.bookmysport.backend.payment.enums.PaymentStatus.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final BookingService bookingService;

    private final SlotLockService slotService;


    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    private final RazorpayService razorpayService;


    @Transactional
    public PaymentResponseDto createPaymentOrder(
            Long bookingId
    ) throws Exception {


        // 1. Get booking
        BookingEntity booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(
                                () -> new ResourseNotFoundException(
                                        "Booking not found"
                                )
                        );


        // 2. Validate booking

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            log.warn("Booking:{} is status is not in Payment_Pending",bookingId);
            throw new BadRequestException(
                    "Booking is not eligible for payment"
            );
        }


        // 3. Create payment record

        PaymentEntity payment =
                PaymentEntity.builder()
                        .booking(booking)
                        .amount(booking.getAmount())
                        .currency("INR")
                        .gateway(PaymentGateway.RAZORPAY)
                        .status(PaymentStatus.CREATED)
                        .build();


        paymentRepository.save(payment);
        log.info("payment is created for the booking:{}",booking.getId());



        // 4. Convert amount to paise

        long amountInPaise =
                payment.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();



        // 5. Create Razorpay order

        Order order =
                razorpayService.createOrder(
                        amountInPaise,
                        "INR",
                        "booking_" + bookingId
                );


        String razorpayOrderId = order.get("id");

        // 6. Save gateway order id

        payment.setGatewayOrderId(razorpayOrderId);
        log.info("PaymentService sent a create order request to razorpay service of booking:{}",booking.getId());


        paymentRepository.save(payment);



        // 7. Return response

        return PaymentResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(razorpayOrderId)
                .amount(amountInPaise)
                .currency("INR")
                .build();

    }

    @Transactional
    public void verifyPayment(VerifyPaymentRequestDto request) throws MessagingException, IOException {

        log.info("payment in verification process");


        PaymentEntity payment = paymentRepository.findById(request.getPaymentId())
                        .orElseThrow(
                                () -> new ResourseNotFoundException(
                                        "Payment not found"
                                )
                        );

        BookingEntity booking = bookingRepository.findById(payment.getBooking().getId())
                .orElseThrow(() -> new ResourseNotFoundException("Booking not found"));

        log.info("received verification detials from Razorpay of payment:{}",request.getPaymentId());


        boolean verified =
                razorpayService.verifyPayment(
                        request.getRazorpayOrderId(),
                        request.getRazorpayPaymentId(),
                        request.getRazorpaySignature()
                );


        if (!verified) {

           payment.setStatus(PaymentStatus.FAILED);
           log.warn("payment:{} verification is failed",request.getPaymentId());
           paymentRepository.save(payment);
           bookingService.cancelBooking(booking.getId(),booking.getUserId());
           throw new BadRequestException("Payment verification failed");}






        payment.setGatewayOrderId(request.getRazorpayOrderId());
        payment.setGatewayPaymentId(request.getRazorpayPaymentId());
        payment.setGatewaySignature(request.getRazorpaySignature());
        payment.setStatus(SUCCESS);

        paymentRepository.save(payment);
        log.info("payment:{} verification is success",request.getPaymentId());
        // 5. Now confirm booking
        bookingService.confirmBooking(booking.getId());
        // 6. Now book slot
        slotService.confirmSlotBooking(booking.getSlotId());

    }
}
