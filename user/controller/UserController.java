package com.bookmysport.backend.user.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.user.dto.Response.UserBookingsResponseDto;
import com.bookmysport.backend.user.dto.Response.UserDashBoardResponse;
import com.bookmysport.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<UserBookingsResponseDto>>>
                                            getUserBookings(@AuthenticationPrincipal SecurityUser securityUser){

        List<UserBookingsResponseDto> userBookings = userService.getUserBooking(securityUser.getUser().getId());
        if (!userBookings.isEmpty()) {

            return ResponseEntity.ok().body(
                    ApiResponse.<List<UserBookingsResponseDto>>builder()
                            .success(true)
                            .message("User Booking Details Feteched Successfully")
                            .timestamp(LocalDateTime.now())
                            .data(userBookings)
                            .build());
        }
        return ResponseEntity.ok().body(
                ApiResponse.<List<UserBookingsResponseDto>>builder()
                        .success(false)
                        .message("No Bookings Yet!")
                        .timestamp(LocalDateTime.now())
                        .data(null)
                        .build());

    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<UserDashBoardResponse>> userDashBoard(@AuthenticationPrincipal SecurityUser securityUser){

        UserDashBoardResponse userDashBoardResponse = userService.userDashBoardResponse(securityUser.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("User Dashboard Details ",userDashBoardResponse));
    }
}
