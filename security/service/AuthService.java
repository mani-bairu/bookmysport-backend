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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final OtpService otpService;

    private final StringRedisTemplate redisTemplate;

    @Value("${slot.lock.minutes}")
    private Integer SLOT_LOCKING_TIME;



    public Boolean userRegister(registerRequestDto request) throws MessagingException, IOException {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("provided Email Already Exists");
        }
        log.info("user:{} registration started",request.getEmail());


        String key = request.getOtp() +":"+ request.getEmail();

        Boolean userRedisOtpKey = redisTemplate.hasKey(key);
        if(!Boolean.TRUE.equals(userRedisOtpKey)){
            log.warn("user:{} sent otp is expired",request.getEmail());
            throw new BadRequestException("Otp Expired - Please Click on Re-Send Otp");

        }
        String serverOtp = redisTemplate.opsForValue().get(key);

        if (!serverOtp.equals(request.getOtp())){
            log.warn("user:{} OTP is Invalid",request.getEmail());
            throw new BadRequestException("Otp Invalid - Please Enter Valid Otp");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info("user:{} Registration Completed Successfully",request.getEmail());

        notificationService.sendWelcomeEmail(user);
        log.info("Welcome email sent to user:{}",request.getEmail());

        return true;

    }

    public Boolean generateOtpAndSendToUserEmail(OtpRequestDto dto) throws MessagingException, IOException {

        String userEmail = dto.getEmail();
        if(userRepository.existsByEmail(userEmail)){
            log.warn("user provided email is already exists",dto.getEmail());
            throw new EmailAlreadyExistsException("Account already Exists with Given Email");
        }

        String otp = otpService.generateOtp();
        log.info("OTP generated for user:{}",dto.getEmail());
        String key = otp +":"+ userEmail;

        Boolean otpLockedForTwoMinutes = redisTemplate.opsForValue()
                .setIfAbsent(key, otp, Duration.ofMinutes(SLOT_LOCKING_TIME));
        if(!Boolean.TRUE.equals(otpLockedForTwoMinutes)){
            System.out.println("redis OTP is Not Locked, Key Already exists");
            throw new EmailAlreadyExistsException("Otp Already Sent!");
        }

        notificationService.sendOtpEmail(userEmail,otp);
        log.info("OTP sent to user:{}",dto.getEmail());

        return true;
    }

    public authResponseDto userLogin(loginRequestDto request){


        log.info("user:{} login request received",request.getEmail());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());
        Authentication authenticate = authManager.authenticate(token);


        SecurityUser securityUser =
                (SecurityUser) authenticate.getPrincipal();

        String responseToken = jwtService.generateToken(securityUser);

        UserEntity user = securityUser.getUser();

        return authResponseDto.builder()
                    .email(securityUser.getUsername())
                    .role(securityUser.getRole())
                    .token(responseToken)
                    .name(securityUser.getUser().getName())
                    .build();

    }

//    forget password


    @Value("${frontend.url}")
    private String baseUrl;

    // Forgot password — send reset link to email
    public void forgotPassword(String email) throws MessagingException, IOException {

        // 1. Check user exists
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourseNotFoundException("No account found with this email"));

        // 2. Generate random token
        String token = UUID.randomUUID().toString();

        // 3. Store in Redis — "reset:{token}" → userId, TTL 15 min
        redisTemplate.opsForValue().set(
                "reset:" + token,
                String.valueOf(user.getId()),
                15, TimeUnit.MINUTES
        );

        // 4. Build reset link
        String resetLink = baseUrl + "/reset-password?token=" + token;

        // 5. Send email
        notificationService.sendPasswordResetEmail(email, user.getName(), resetLink);

        log.info("Password reset link sent to user:{}", email);
    }

    // Reset password — update password using token
    public void resetPassword(String token, String newPassword) {

        // 1. Get userId from Redis
        String userId = redisTemplate.opsForValue().get("reset:" + token);

        if (userId == null) {
            throw new BadRequestException("Reset link has expired or is invalid. Please request a new one.");
        }

        // 2. Find user
        UserEntity user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));

        // 3. Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset successfully for user:{}", user.getEmail());

        // 4. Delete token from Redis — single use
        redisTemplate.delete("reset:" + token);
        log.info("Reset token deleted from Redis");
    }
}
