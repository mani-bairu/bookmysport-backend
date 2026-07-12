package com.bookmysport.backend.slot.scheduler;

import com.bookmysport.backend.slot.service.SlotLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReleaseExpiredSlotLockScheduler {

    private final SlotLockService slotLockService;

//    @Value("${release.expired.slots}")
//    private Long RUN_RELEASE_EXPIRED_SLOTS;

    @Scheduled(fixedDelayString = "${release.expired.slots}")
    public void releaseExpiredSlots(){

        slotLockService.releaseExpiredSlot();


    }
}
