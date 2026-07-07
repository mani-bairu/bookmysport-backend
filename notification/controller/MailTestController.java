package com.bookmysport.backend.notification.controller;

import com.bookmysport.backend.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class MailTestController {


    private final EmailService emailService;


    @GetMapping("/email")
    public String send(){

//        emailService.sendEmail(
//                "bairumanideepgoud@gmail.com",
//                "Test Email",
//                "BookMySport email working"
//        );

        return "sent";
    }
}



