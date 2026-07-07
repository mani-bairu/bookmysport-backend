package com.bookmysport.backend.common.ResponseApiDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T>{

    private Boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

}
