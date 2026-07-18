package com.bookmysport.backend.security.dtos.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgetPasswordRequestDto {

    @Email
    @NotBlank
    private String email;
}
