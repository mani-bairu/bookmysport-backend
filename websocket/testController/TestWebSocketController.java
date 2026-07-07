package com.bookmysport.backend.websocket.testController;

import com.bookmysport.backend.websocket.dto.SlotEvent;
import com.bookmysport.backend.websocket.service.SlotWebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestWebSocketController {

    private final SlotWebSocketService webSocketService;

    public TestWebSocketController(SlotWebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @GetMapping("/websocket")
    public String test() {

        SlotEvent event = new SlotEvent(
                101L,
                "LOCKED",
                21L
        );

        webSocketService.sendSlotEvent(event);

        return "Event sent";
    }
}
