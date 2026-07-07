package com.bookmysport.backend.security.service;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.security.dtos.requestDto.loginRequestDto;
import com.bookmysport.backend.security.dtos.requestDto.registerRequestDto;
import com.bookmysport.backend.security.dtos.responseDto.authResponseDto;
import com.bookmysport.backend.security.jwt.JwtService;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.enums.Role;
import com.bookmysport.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;



    public authResponseDto userRegister(registerRequestDto request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("provided Email Already Exists");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return authResponseDto.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

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
