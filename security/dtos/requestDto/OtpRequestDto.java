package com.bookmysport.backend.security.dtos.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequestDto {

    @Email(message = "Invalid Email!")
    @NotBlank(message = "Email is required!")
    private String email;
}
