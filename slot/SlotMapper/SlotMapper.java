package com.bookmysport.backend.slot.SlotMapper;

import com.bookmysport.backend.slot.dto.SlotResponseDto;
import com.bookmysport.backend.slot.entity.SlotEntity;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {

    public SlotResponseDto entityToResponseDto(SlotEntity slot){
        return SlotResponseDto.builder()
                .id(slot.getId())
                .sportAreaId(slot.getSportAreaId())
                .venueId(slot.getVenueId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus())
                .build();
    }
}
