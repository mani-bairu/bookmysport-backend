package com.bookmysport.backend.user.mapper.UserMapper;

import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.user.dto.Response.UserBookingsResponseDto;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.entity.VenueEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserBookingsResponseDto entityToResonseDto(BookingEntity entity, SlotEntity slot, VenueEntity venue, SportAreaEntity sportArea){
        return UserBookingsResponseDto.builder()
                .bookingDate(entity.getCreatedAt())
                .amountPaid(entity.getAmount())
                .bookingId(entity.getId())
                .SlotBookedDate(slot.getDate())
                .slotStartTime(slot.getStartTime())
                .slotEndTime(slot.getEndTime())
                .sportAreaName(sportArea.getSportAreaName())
                .venueName(venue.getVenueName())
                .build();

    }
}
