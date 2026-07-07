package com.bookmysport.backend.venue.dto.requestdto;

import com.bookmysport.backend.common.enums.DayType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class UpdateSportAreaRequestDto {

    private DayType dayType;      // null means don't change
    private BigDecimal price;     // null means don't change
    private LocalDate validFrom;  // null means don't change
}
