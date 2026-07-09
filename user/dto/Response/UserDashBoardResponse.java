package com.bookmysport.backend.user.dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDashBoardResponse {

    private String name;

    private String email;
}
