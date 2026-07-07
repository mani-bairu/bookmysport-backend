package com.bookmysport.backend.booking.dto;

import com.bookmysport.backend.common.enums.BookingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class BookingResponseDto {


    private Long id;

    private Long userId;

    private Long slotId;

    // PENDING_PAYMENT, CONFIRMED, CANCELLED
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private BigDecimal amount;

    private LocalDate bookedDate;

    private LocalTime slotStartTime;

    private LocalTime slotEndTime;

}
