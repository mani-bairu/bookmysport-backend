package com.bookmysport.backend.payment.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RazorpayConfig {


    @Value("${razorpay.key-id}")
    private String keyId;


    @Value("${razorpay.key-secret}")
    private String keySecret;

}
