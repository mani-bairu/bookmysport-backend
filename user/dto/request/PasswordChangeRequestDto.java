package com.bookmysport.backend.user.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordChangeRequestDto {

    @NotEmpty(message = "Old password required!")
    private String oldPassword;

    @NotEmpty(message = "New Password Required!")
    @Min(8)
    private String newPassword;
}
