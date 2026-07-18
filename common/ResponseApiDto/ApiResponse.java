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


    // static factory methods
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error( T data) {
        return new ApiResponse<>(false, null, data, LocalDateTime.now());
    }

}
