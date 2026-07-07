package com.bookmysport.backend.slot.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.slot.dto.SlotResponseDto;
import com.bookmysport.backend.slot.scheduler.SlotGenerationScheduler;
import com.bookmysport.backend.slot.service.SlotLockService;
import com.bookmysport.backend.slot.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slot")
public class SlotController {

    private final SlotGenerationScheduler slotGenerationScheduler;

    private final SlotService slotService;

    private final SlotLockService slotLockService;

    @GetMapping("/runSlotGenerator")
    public String runSlotSchedulerTest(){

        slotGenerationScheduler.generateSlot();

        return "OK";
    }


    @GetMapping("/{sportAreaId}/{date}")
    public ResponseEntity<ApiResponse<List<SlotResponseDto>>> getSlots(
            @PathVariable Long sportAreaId,
            @PathVariable LocalDate date) {

        List<SlotResponseDto> slotsBySportAreaIdAndDate = slotService.
                getSlotsBySportAreaIdAndDate(sportAreaId, date);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<SlotResponseDto>>builder()
                        .success(true)
                        .message("Here the slot you are requested for!!")
                        .timestamp(LocalDateTime.now())
                        .data(slotsBySportAreaIdAndDate)
                        .build());
    }

    @PostMapping("/{slotId}/lock")
    public ResponseEntity<String> lockSlot(
            @PathVariable Long slotId,
            @AuthenticationPrincipal SecurityUser securityUser){

        Boolean success = slotLockService.lockSlot(slotId, securityUser.getUser().getId());

        return success
                ? ResponseEntity.ok("Slot Locked")
                : ResponseEntity.badRequest().body("Cannot lock slot");

    }
}
