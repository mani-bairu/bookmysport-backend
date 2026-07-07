package com.bookmysport.backend.slot.scheduler;


import com.bookmysport.backend.slot.service.SlotGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlotGenerationScheduler {

    private final SlotGenerationService slotGenerationService;

//    @Scheduled(cron = "0 0 1 * * *") // Every day at 1:00 AM
    public void generateSlot(){

        log.info("========== Slot Generation Started ==========");

        slotGenerationService.generateSlot();

        log.info("========== Slot Generation Completed ==========");
    }
}
