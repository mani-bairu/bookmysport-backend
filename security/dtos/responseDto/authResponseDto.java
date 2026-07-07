package com.bookmysport.backend.security.dtos.responseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class authResponseDto {

    private String token;
    private String email;
    private String role;


}
