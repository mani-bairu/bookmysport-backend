package com.bookmysport.backend.security.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpService {
    
    private static final SecureRandom random = new SecureRandom();

    public String generateOtp(){
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
