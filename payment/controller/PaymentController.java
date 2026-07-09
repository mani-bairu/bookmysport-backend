package com.bookmysport.backend.payment.controller;



import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.payment.dto.Response.PaymentResponseDto;
import com.bookmysport.backend.payment.dto.request.VerifyPaymentRequestDto;
import com.bookmysport.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;


    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponseDto> createPaymentOrder(
            @RequestParam Long bookingId
    ) throws Exception {

        PaymentResponseDto response =
                paymentService.createPaymentOrder(bookingId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyPayment(
            @RequestBody VerifyPaymentRequestDto request
    ) {

        paymentService.verifyPayment(request);

        return ResponseEntity.ok().body(
                ApiResponse.<String>builder()
                        .message("Success")
                        .success(true)
                        .data("Payment successfully completed")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
