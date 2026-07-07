package com.bookmysport.backend.booking.controller;

import com.bookmysport.backend.booking.dto.BookingRequestDto;
import com.bookmysport.backend.booking.dto.BookingResponseDto;
import com.bookmysport.backend.booking.service.BookingService;
import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.models.SecurityUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @RequestBody BookingRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser

    ){
        BookingResponseDto booking = bookingService.createBooking(dto.getSlotId(), securityUser.getUser().getId());

        return ResponseEntity.ok()
                .body(ApiResponse.<BookingResponseDto>builder()
                        .success(true)
                        .message("Booking created!")
                        .timestamp(LocalDateTime.now())
                        .data(booking)
                        .build());
    }
}
