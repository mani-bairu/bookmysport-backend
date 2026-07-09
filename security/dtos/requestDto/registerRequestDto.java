package com.bookmysport.backend.security.dtos.requestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class registerRequestDto {

    @NotBlank(message = "Name is Required")
    private String name;

    @Email(message = "invalid Email")
    @NotBlank(message = "Email is Required")
    private String email;

    @Size(min = 8,message = "Password must be atleast 8 characters")
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Otp is Required")
    @Size(min = 6, max = 6)
    private String otp;

}
