package com.bookmysport.backend.venue.mapper;

import com.bookmysport.backend.venue.dto.requestdto.CreatePricingRuleRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.PricingRuleResponseDto;
import com.bookmysport.backend.venue.entity.PricingRuleEntity;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import lombok.Data;

@Data
public class PricingRuleMapper {

    public static PricingRuleEntity toEntity(CreatePricingRuleRequestDto dto, SportAreaEntity sportArea){
        return PricingRuleEntity.builder()
                .sportArea(sportArea)
                .dayType(dto.getDayType())
                .timeBand(dto.getTimeBand())
                .bandStart(dto.getBandStart())
                .bandEnd(dto.getBandEnd())
                .price(dto.getPrice())
                .validFrom(dto.getValidFrom())
                .build();
    }

    public static PricingRuleResponseDto toResponseDto(PricingRuleEntity dto){
        return PricingRuleResponseDto.builder()
                .id(dto.getId())
                .dayType(dto.getDayType())
                .timeBand(dto.getTimeBand())
                .bandStart(dto.getBandStart())
                .bandEnd(dto.getBandEnd())
                .price(dto.getPrice())
                .validFrom(dto.getValidFrom())
                .build();

    }
}
