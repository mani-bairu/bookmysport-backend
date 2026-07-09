package com.bookmysport.backend.slot.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.exception.BadRequestException;
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
@RequestMapping("/api/v1/slots")
public class SlotController {

    private final SlotGenerationScheduler slotGenerationScheduler;

    private final SlotService slotService;

    private final SlotLockService slotLockService;

    @GetMapping("/runSlotGenerator")
    public String runSlotSchedulerTest(){

        slotGenerationScheduler.generateSlot();

        return "OK";
    }


    @GetMapping()
    public ResponseEntity<ApiResponse<List<SlotResponseDto>>> getSlots(
            @RequestParam Long sportAreaId,
            @RequestParam LocalDate date) {

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
    public ResponseEntity<ApiResponse<String>> lockSlot(
            @PathVariable Long slotId,
            @AuthenticationPrincipal SecurityUser securityUser){

        Boolean success = slotLockService.lockSlot(slotId, securityUser.getUser().getId());

        return success
                ? ResponseEntity.ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Slot Locked")
                        .data("Slot Locked Sucessfully")
                        .timestamp(LocalDateTime.now())
                        .build())
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Slot is not Locked")
                        .data("Slot is not locked")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
