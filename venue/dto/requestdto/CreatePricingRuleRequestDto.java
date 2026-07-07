package com.bookmysport.backend.venue.dto.requestdto;

import com.bookmysport.backend.common.enums.DayType;
import com.bookmysport.backend.common.enums.TimeBand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreatePricingRuleRequestDto {

    @NotNull(message = "Day type is required")
    private DayType dayType;       // WEEKDAY | WEEKEND | HOLIDAY

    @NotNull(message = "Time band is required")
    private TimeBand timeBand;     // MORNING | EVENING | NIGHT

    @NotNull(message = "Band start is required")
    private LocalTime bandStart;   // e.g. "06:00"

    @NotNull(message = "Band end is required")
    private LocalTime bandEnd;     // e.g. "12:00"

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    private LocalDate validTo;     // null means no expiry
}
