package com.bookmysport.backend.user.service;

import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.booking.repository.BookingRepository;
import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.user.dto.Response.UserBookingsResponseDto;
import com.bookmysport.backend.user.dto.Response.UserDashBoardResponse;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.mapper.UserMapper.UserMapper;
import com.bookmysport.backend.user.repository.UserRepository;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.entity.VenueEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;

   private final UserMapper userMapper;


   public List<UserBookingsResponseDto> getUserBooking(Long userId){


       UserEntity userEntity = userRepository.findById(userId)
               .orElseThrow(()-> new ResourseNotFoundException("User Not Found"));

       ArrayList<BookingStatus> statuses = new ArrayList<>(Arrays.asList(
               BookingStatus.CONFIRMED,
               BookingStatus.CANCELLED
       ));

       List<UserBookingsResponseDto> userBookings = userRepository.
               userBookingList(userEntity.getId(), statuses);
       return userBookings;

   }


   public UserDashBoardResponse userDashBoardResponse(Long userId){
       UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new ResourseNotFoundException("User Not Found"));
       return userMapper.entityToDashBoardResponseDto(userEntity);
   }
}
