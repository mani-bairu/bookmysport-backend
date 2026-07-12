package com.bookmysport.backend.booking.controller;

import com.bookmysport.backend.booking.dto.BookingRequestDto;
import com.bookmysport.backend.booking.dto.BookingResponseDto;
import com.bookmysport.backend.booking.service.BookingService;
import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.models.SecurityUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/booking")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @RequestBody BookingRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser

    ){
        log.info(" booking request — slotId:{} userId:{}",dto.getSlotId(),securityUser.getUser().getId());

        BookingResponseDto booking = bookingService.createBooking(dto.getSlotId(), securityUser.getUser().getId());

        return ResponseEntity.ok()
                .body(ApiResponse.<BookingResponseDto>builder()
                        .success(true)
                        .message("Booking created!")
                        .timestamp(LocalDateTime.now())
                        .data(booking)
                        .build());
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Cancel booking request — bookingId:{} userId:{}",
                bookingId, securityUser.getUser().getId());

        bookingService.cancelBooking(bookingId, securityUser.getUser().getId());

        return ResponseEntity.ok(
                ApiResponse.success("Booking cancelled successfully", null)
        );
    }
}
