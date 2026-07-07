package com.bookmysport.backend.slot.dto;

import com.bookmysport.backend.slot.entity.SlotStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class SlotResponseDto {

    private Long id;

    private Long venueId;

    private Long sportAreaId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;
}
