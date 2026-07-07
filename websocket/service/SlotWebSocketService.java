package com.bookmysport.backend.websocket.service;

import com.bookmysport.backend.websocket.dto.SlotEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlotWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public SlotWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendSlotEvent(SlotEvent event) {

        messagingTemplate.convertAndSend(
                "/topic/slots",
                event
        );
    }
}