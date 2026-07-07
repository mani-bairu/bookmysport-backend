package com.bookmysport.backend.venue.dto.responsedto;

import com.bookmysport.backend.common.enums.DayType;
import com.bookmysport.backend.common.enums.TimeBand;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class PricingRuleResponseDto {

    private Long       id;
    private DayType dayType;
    private TimeBand timeBand;
    private LocalTime  bandStart;
    private LocalTime bandEnd;
    private BigDecimal price;
    private LocalDate validFrom;
    private LocalDate  validTo;
}
