package com.bookmysport.backend.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotEvent {

    private Long slotId;
    private String eventType; // LOCKED / BOOKED / RELEASED
    private Long userId;
}
