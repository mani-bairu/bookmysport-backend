package com.bookmysport.backend.slot.service;

import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.slot.SlotMapper.SlotMapper;
import com.bookmysport.backend.slot.dto.SlotResponseDto;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import com.bookmysport.backend.slot.respository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;

    private final SlotMapper slotMapper;

    public List<SlotResponseDto> getSlotsBySportAreaIdAndDate(Long sportAreaId, LocalDate date){
        if (!slotRepository.existsBySportAreaIdAndDate(sportAreaId,date)){
            throw new ResourseNotFoundException("slots not found for given SportArea and date:"+ sportAreaId + "and date: " +date);
        }
        List<SlotEntity> bySportAreaIdAndDate = slotRepository.findBySportAreaIdAndDate(sportAreaId, date);

        return bySportAreaIdAndDate.stream()
                .map(slotMapper::entityToResponseDto)
                .toList();
    }


}
