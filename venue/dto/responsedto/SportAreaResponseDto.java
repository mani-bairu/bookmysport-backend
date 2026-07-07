package com.bookmysport.backend.venue.dto.responsedto;

import com.bookmysport.backend.common.enums.AreaStatus;
import com.bookmysport.backend.common.enums.SportType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class SportAreaResponseDto {

    private Long        id;
    private String      name;
    private SportType sportType;
    private String      description;
    private Integer     capacity;
    private LocalTime   openingTime;
    private LocalTime   closingTime;
    private Integer     durationMinutes;
    private AreaStatus status;
    private List<PricingRuleResponseDto> pricingRules;
}
