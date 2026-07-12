package com.bookmysport.backend.booking.service;

import com.bookmysport.backend.booking.dto.BookingDetailsDto;
import com.bookmysport.backend.booking.dto.BookingResponseDto;
import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.booking.mapper.BookingMapper;
import com.bookmysport.backend.booking.repository.BookingRepository;
import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.BookingException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.notification.service.NotificationService;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import com.bookmysport.backend.slot.respository.SlotRepository;
import com.bookmysport.backend.websocket.dto.SlotEvent;
import com.bookmysport.backend.websocket.service.SlotWebSocketService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookingService {

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redisTemplate;
    private final SlotWebSocketService webSocketService;
    private final BookingMapper bookingMapper;

    private final NotificationService notificationService;

    public BookingResponseDto createBooking(Long slotId, Long userId) {

        log.info("creating Booking of user:{} and slot:{} ",userId,slotId);

       SlotEntity slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // 1. Must be LOCKED
        if (slot.getStatus() != SlotStatus.LOCKED) {
            log.warn("user:{} Tried Booking Locked slot:{}",userId,slotId);
            throw new BadRequestException("Slot not locked");
        }

        // 2. Must be locked by same user
        if (!userId.equals(slot.getLockedByUser())) {
            log.warn("slot:{} is not locked by user:{} ",slotId,userId);
            throw new BadRequestException("Slot locked by another user");
        }

        // 3. Check Redis lock still exists
        String key = "lock:" + slotId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            log.warn("user:{} booking slot:{} is already expired ",userId,slotId);

            throw new BadRequestException("Lock expired");
        }


        // 4. Create booking
        BookingEntity booking = BookingEntity.builder()
                .slotId(slotId)
                .userId(userId)
                .status(BookingStatus.PENDING_PAYMENT)
                .amount(slot.getPrice())
                .bookedDate(slot.getDate())
                .slotStartTime(slot.getStartTime())
                .slotEndTime(slot.getEndTime())
                .build();

        bookingRepository.save(booking);
        log.info("Booking created for user:{} slot:{}",userId,slotId);

        BookingResponseDto bookingResponseDto = bookingMapper.entityToResponseDto(booking);


        // 5. Notify frontend
        webSocketService.sendSlotEvent(
                new SlotEvent(slotId, "BOOKING_CREATED", userId)
        );

        return bookingResponseDto;
    }





    @Transactional
    public BookingEntity confirmBooking(Long bookingId) throws MessagingException, IOException {


        BookingEntity booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(
                                () -> new ResourseNotFoundException(
                                        "Booking not found"

                                )
                        );



        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {

            log.warn("booking:{} is not waiting for the payment",booking.getId());

            throw new BadRequestException(
                    "Booking is not waiting for payment"

            );
        }


        booking.setStatus(
                BookingStatus.CONFIRMED
        );

      BookingEntity userBooking = bookingRepository.findById(bookingId)
              .orElseThrow(()-> new ResourseNotFoundException("booking not found"));

        BookingDetailsDto bookingDetailsDto = bookingRepository.userBookedDetails(bookingId);

        notificationService.sendBookingConfirmation(bookingDetailsDto);

        BookingEntity save = bookingRepository.save(booking);
        log.info("Booking:{} is confirmed of user:{}",save.getId(),save.getUserId());

        return save;

    }

//    booking cancelation

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {

        log.info("Cancelling booking:{} for user:{}", bookingId, userId);

        // 1. Find booking
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourseNotFoundException("Booking not found"));

        // 2. Check booking belongs to this user
        if (!booking.getUserId().equals(userId)) {
            log.warn("user:{} tried to cancel booking:{} belonging to another user",
                    userId, bookingId);
            throw new BadRequestException("Not authorized to cancel this booking");
        }

        // 3. Check booking can be cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Booking:{} is already cancelled", bookingId);
            throw new BadRequestException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            log.warn("Booking:{} is already confirmed — cannot cancel", bookingId);
            throw new BadRequestException("Cannot cancel a confirmed booking");
        }

        // 4. Cancel booking — only PENDING_PAYMENT bookings can be cancelled
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking:{} status set to CANCELLED", bookingId);

        // 5. Release slot
        SlotEntity slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new ResourseNotFoundException("Slot not found"));

        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setLockedByUser(null);
        slotRepository.save(slot);
        log.info("Slot:{} released back to AVAILABLE", slot.getId());

        // 6. Delete Redis lock key
        String redisKey = "lock:" + booking.getSlotId();
        redisTemplate.delete(redisKey);
        log.info("Redis lock deleted for slot:{}", slot.getId());

        // 7. Notify all users via WebSocket
        webSocketService.sendSlotEvent(
                new SlotEvent(booking.getSlotId(), "RELEASED", userId)
        );

        log.info("Booking:{} cancelled and slot:{} released successfully",
                bookingId, booking.getSlotId());
    }
}
