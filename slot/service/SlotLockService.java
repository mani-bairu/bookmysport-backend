package com.bookmysport.backend.slot.service;

import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import com.bookmysport.backend.slot.respository.SlotRepository;
import com.bookmysport.backend.websocket.dto.SlotEvent;
import com.bookmysport.backend.websocket.service.SlotWebSocketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotLockService {

    private final SlotWebSocketService slotWebSocketService;
    private final StringRedisTemplate redisTemplate;
    private final SlotRepository slotRepository;

    public Boolean lockSlot(Long slotId, Long userId){

        SlotEntity slot = slotRepository.findById(slotId).orElseThrow(
                () -> new ResourseNotFoundException("No slot found with the given Slot Id : " + slotId));

       if(slot.getStatus()== SlotStatus.BOOKED){
           return false;
       }
        if(slot.getStatus()== SlotStatus.LOCKED){
            if(slot.getLockExpiresAt() != null &&
                    LocalDateTime.now().isAfter(slot.getLockExpiresAt()) )
                releaseSlot(slot);
            return false;
        }



        // 2. Try Redis lock (race condition protection)
       String key = "lock:" + slotId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(key, userId.toString(), Duration.ofMinutes(5));

        if (!Boolean.TRUE.equals(locked)){
            return false;
        }
        try {

            // 3. Update DB
            slot.setStatus(SlotStatus.LOCKED);
            slot.setLockedByUser(userId);
            slot.setLockExpiresAt(LocalDateTime.now().plusMinutes(5));

            slotRepository.save(slot);

            // 4. Send WebSocket event
            slotWebSocketService.sendSlotEvent(
                    new SlotEvent(slotId,"LOCKED",userId)
            );

            return true;


        } catch (Exception e) {
            redisTemplate.delete(key);
            throw e;
        }

    }


    public void releaseSlot(SlotEntity slot){

        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setLockExpiresAt(null);
        slot.setLockedByUser(null);
        slotRepository.save(slot);

        redisTemplate.delete("lock:" + slot.getId());

        slotWebSocketService.sendSlotEvent(
                new SlotEvent(slot.getId(), "RELEASED",null)
        );


    }

    public void releaseExpiredSlot(){

        List<SlotEntity> expiredSlots = slotRepository.findByStatusAndLockExpiresAtBefore(
                                            SlotStatus.LOCKED,
                                            LocalDateTime.now());


        for (SlotEntity slot : expiredSlots) {
            releaseSlot(slot);
        }
    }



    @Transactional
    public void confirmSlotBooking(Long slotId) {


        SlotEntity slot =
                slotRepository.findById(slotId)
                        .orElseThrow(
                                () -> new ResourseNotFoundException(
                                        "Slot not found"
                                )
                        );


        if(slot.getStatus() != SlotStatus.LOCKED){

            throw new BadRequestException(
                    "Slot is not locked"
            );
        }


        slot.setStatus(
                SlotStatus.BOOKED
        );


        slot.setLockedByUser(null);

        slot.setLockExpiresAt(null);


        slotRepository.save(slot);


        redisTemplate.delete(
                "lock:" + slotId
        );


        slotWebSocketService.sendSlotEvent(
                new SlotEvent(slotId, "BOOKED",null));
    }

}
