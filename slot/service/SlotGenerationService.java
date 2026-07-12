package com.bookmysport.backend.slot.service;

import com.bookmysport.backend.slot.entity.SlotGenerationTrackerEntity;
import com.bookmysport.backend.slot.respository.SlotGenerationTrackerRepository;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.repository.SportAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotGenerationService {

    @Value("${generate.slots.days.ahead}")
    private int SLOT_WINDOW_DAYS;

    private final SportAreaRepository sportAreaRepository;

    private final SlotGenerationTrackerRepository trackerRepository;

    private final SlotGenerator slotGenerator;


    public void generateSlot(){

        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(SLOT_WINDOW_DAYS-1);

        List<SportAreaEntity> allValidSportArea = sportAreaRepository.findAllValidSportAreas();

        if (allValidSportArea.isEmpty()) {
            log.info("Nightly generation: no eligible sport areas found.");
            return;
        }

        for(SportAreaEntity sportArea : allValidSportArea){

            processSportArea(sportArea,today,windowEnd);
        }


    }

    public void processSportArea(SportAreaEntity sportArea,LocalDate today,LocalDate windowEnd){


       SlotGenerationTrackerEntity tracker = trackerRepository.findBySportAreaId(sportArea.getId())
                .orElseGet(()-> {
                    SlotGenerationTrackerEntity t = new SlotGenerationTrackerEntity();
                    t.setSportAreaId(sportArea.getId());
                    return t;
                });

       LocalDate startDate;

       if( tracker.getGeneratedUntilDate() == null){
           startDate = today;
       }else{
           startDate = tracker.getGeneratedUntilDate().plusDays(1);
       }

       if (startDate.isAfter(windowEnd)){

           log.info("Slots already generated for Sport Area : {}",
                   sportArea.getId());
           return;
       }

       slotGenerator.generate(sportArea,startDate,windowEnd);
        tracker.setGeneratedUntilDate(windowEnd);
        trackerRepository.save(tracker);
        log.info("Slot generation completed for Sport Area : {}",
                sportArea.getId());


    }


}
