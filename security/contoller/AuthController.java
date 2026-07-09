package com.bookmysport.backend.security.contoller;


import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.dtos.requestDto.OtpRequestDto;
import com.bookmysport.backend.security.dtos.requestDto.loginRequestDto;
import com.bookmysport.backend.security.dtos.requestDto.registerRequestDto;
import com.bookmysport.backend.security.dtos.responseDto.authResponseDto;
import com.bookmysport.backend.security.service.AuthService;
import com.bookmysport.backend.security.utils.OtpService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Boolean>> register(@Valid @RequestBody registerRequestDto request) throws MessagingException, IOException {

        Boolean response = authService.userRegister(request);

        return ResponseEntity.ok(ApiResponse.success("Success",response));


    }

    @PostMapping("/otp")
    public ResponseEntity<ApiResponse<Boolean>>generateOTP(@Valid @RequestBody OtpRequestDto dto) throws MessagingException, IOException {

        Boolean b = authService.generateOtpAndSendToUserEmail(dto);

        return ResponseEntity.ok(ApiResponse.success("Success",b));



    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<authResponseDto>> login(
            @Valid
            @RequestBody loginRequestDto request
    ) {


        authResponseDto response = authService.userLogin(request);

        ApiResponse<authResponseDto> apiResponse = ApiResponse.<authResponseDto>builder()
                .success(true)
                .message("Login Successful!")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);


    }

    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/welcome_owner")
    public String welcomeOwner(){
        return "Only Owner - welcome to BookMySport";
    }

    @PreAuthorize("hasAnyAuthority('OWNER','USER')")
    @GetMapping("/welcome_user")
    public String welcomeUser(){
        return "User - welcome to BookMySport";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/welcome_ADMIN")
    public String welcomeAdmin(){
        return "Only Admin  - welcome to BookMySport";
    }
}
