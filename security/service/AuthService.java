package com.bookmysport.backend.security.service;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.EmailAlreadyExistsException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.notification.service.NotificationService;
import com.bookmysport.backend.security.dtos.requestDto.OtpRequestDto;
import com.bookmysport.backend.security.dtos.requestDto.loginRequestDto;
import com.bookmysport.backend.security.dtos.requestDto.registerRequestDto;
import com.bookmysport.backend.security.dtos.responseDto.authResponseDto;
import com.bookmysport.backend.security.jwt.JwtService;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.security.utils.OtpService;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.enums.Role;
import com.bookmysport.backend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final OtpService otpService;

    private final StringRedisTemplate redisTemplate;



    public Boolean userRegister(registerRequestDto request) throws MessagingException, IOException {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("provided Email Already Exists");
        }
        System.out.println("otp received from user"+request.getOtp());

        String key = request.getOtp() +":"+ request.getEmail();

        Boolean userRedisOtpKey = redisTemplate.hasKey(key);
        if(!Boolean.TRUE.equals(userRedisOtpKey)){
            throw new BadRequestException("Otp Expired - Please Click on Re-Send Otp");
        }
        String serverOtp = redisTemplate.opsForValue().get(key);
        System.out.println("otp received from server "+serverOtp);

        if (!serverOtp.equals(request.getOtp())){
            throw new BadRequestException("Otp Invalid - Please Enter Valid Otp");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        notificationService.sendWelcomeEmail(user);

        return true;

    }

    public Boolean generateOtpAndSendToUserEmail(OtpRequestDto dto) throws MessagingException, IOException {

        String userEmail = dto.getEmail();
        if(userRepository.existsByEmail(userEmail)){
            throw new EmailAlreadyExistsException("Account already Exists with Given Email");
        }

        String otp = otpService.generateOtp();
        String key = otp +":"+ userEmail;

        Boolean otpLockedForTwoMinutes = redisTemplate.opsForValue()
                .setIfAbsent(key, otp, Duration.ofMinutes(2));
        if(!Boolean.TRUE.equals(otpLockedForTwoMinutes)){
            System.out.println("redis OTP is Not Locked, Key Already exists");
            throw new EmailAlreadyExistsException("Otp Already Sent!");
        }

        notificationService.sendOtpEmail(userEmail,otp);

        return true;
    }

    public authResponseDto userLogin(loginRequestDto request){


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());
        Authentication authenticate = authManager.authenticate(token);

        SecurityUser securityUser =
                (SecurityUser) authenticate.getPrincipal();

        String responsetoken = jwtService.generateToken(securityUser);

        UserEntity user = securityUser.getUser();

        return authResponseDto.builder()
                    .email(securityUser.getUsername())
                    .role(securityUser.getRole())
                    .token(responsetoken)
                    .build();

    }
}
