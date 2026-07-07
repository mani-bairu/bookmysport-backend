package com.bookmysport.backend.slot.scheduler;

import com.bookmysport.backend.slot.service.SlotLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReleaseExpiredSlotLockScheduler {

    private final SlotLockService slotLockService;



    @Scheduled(fixedDelay = 60000)
    public void releaseExpiredSlots(){

        slotLockService.releaseExpiredSlot();


    }
}
