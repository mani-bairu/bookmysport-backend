package com.bookmysport.backend.venue.dto.requestdto;

import com.bookmysport.backend.common.enums.SportType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateSportAreaRequestDto {

    @NotBlank(message = "Area name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Sport type is required")
    private SportType sportType;

    private String description;

    @Min(1) @Max(100)
    private Integer capacity = 1;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;     // e.g. "06:00"

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;     // e.g. "23:00"

    @NotNull(message = "Duration is required")
    @Min(15) @Max(240)
    private Integer durationMinutes;   // e.g. 60
}
