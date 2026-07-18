package com.bookmysport.backend.venue.dto.responsedto;

import com.bookmysport.backend.common.enums.SportType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class VenueSummeryResponseDto {

    private Long id;
    private String name;
    private String city;
    private String address;
    private List<SportType> sports;
}
