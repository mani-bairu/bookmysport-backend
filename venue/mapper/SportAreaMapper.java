package com.bookmysport.backend.venue.mapper;

import com.bookmysport.backend.common.enums.DayType;
import com.bookmysport.backend.venue.dto.requestdto.CreateSportAreaRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.UpdateSportAreaRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.PricingRuleResponseDto;
import com.bookmysport.backend.venue.dto.responsedto.SportAreaResponseDto;
import com.bookmysport.backend.venue.entity.PricingRuleEntity;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.entity.VenueEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class SportAreaMapper {

    public static SportAreaEntity toEntity(CreateSportAreaRequestDto sportAreaDto, VenueEntity venue){
        return SportAreaEntity.builder()
                .venue(venue)
                .sportAreaName(sportAreaDto.getName())
                .sportType(sportAreaDto.getSportType())
                .description(sportAreaDto.getDescription())
                .capacity(sportAreaDto.getCapacity())
                .openingTime(sportAreaDto.getOpeningTime())
                .closingTime(sportAreaDto.getClosingTime())
                .durationMinutes(sportAreaDto.getDurationMinutes())
                .build();
    }

    public static SportAreaResponseDto toResponseDto(SportAreaEntity dto){

        List<PricingRuleResponseDto> pricingRules = dto.getPricingRules()
                .stream()
                .map(PricingRuleMapper::toResponseDto)
                .toList();

        return SportAreaResponseDto.builder()
                .id(dto.getId())
                .name(dto.getSportAreaName())
                .sportType(dto.getSportType())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .durationMinutes(dto.getDurationMinutes())
                .status(dto.getStatus())
                .pricingRules(pricingRules)
                .build();
    }

//    private DayType dayType;      // null means don't change
//    private BigDecimal price;     // null means don't change
//    private LocalDate validFrom;

//    public static SportAreaResponseDto toUpdateResponseDto(UpdateSportAreaRequestDto dto,SportAreaEntity sportArea){
//        if(dto.getDayType()!=null){
//            sportAre
//        }
//    }
}
