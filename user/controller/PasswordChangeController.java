package com.bookmysport.backend.user.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.user.dto.request.PasswordChangeRequestDto;
import com.bookmysport.backend.user.service.UserPasswordChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class PasswordChangeController {

    private final UserPasswordChangeService userPasswordChangeService;

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PasswordChangeRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser
            ){

        String oldToken = authHeader.substring(7);
        String newToken = userPasswordChangeService.changePassword(dto, securityUser.getUser().getId(), oldToken);

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully",newToken));
    }
}
