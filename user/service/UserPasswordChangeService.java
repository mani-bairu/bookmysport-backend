package com.bookmysport.backend.user.service;


import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.security.jwt.JwtService;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.user.dto.request.PasswordChangeRequestDto;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordChangeService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String changePassword(PasswordChangeRequestDto dto,Long userId,String oldToken){

//        checks if user is valid user
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(()
                -> new ResourseNotFoundException("User Not Found"));

//        check if the old password is valid
        if(!passwordEncoder.matches(dto.getOldPassword(), userEntity.getPassword())){
            throw new BadRequestException("User Old password is Incorrect");
        }

//        Update new password to DB
        userEntity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(userEntity);

//        adding user old password to blacklist
        long tokenExpireTime = jwtService.extractExpiration(oldToken).getTime();
        Long ttl = (tokenExpireTime - System.currentTimeMillis())/1000;
        try{
            if (ttl>0){

                redisTemplate.opsForValue().set(
                        "blackList:" + oldToken,
                        "Revoked",
                        ttl, TimeUnit.SECONDS);
            }
            log.info("Token already expired!");

        } catch (Exception e) {
            log.warn("Exception occurred in token adding to blacklist");
            throw new RuntimeException(e);
        }

        SecurityUser securityUser = new SecurityUser(userEntity);

//        return new token to user
        return jwtService.generateToken(securityUser);



    }
}
