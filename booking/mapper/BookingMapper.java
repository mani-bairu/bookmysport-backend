package com.bookmysport.backend.booking.mapper;

import com.bookmysport.backend.booking.dto.BookingResponseDto;
import com.bookmysport.backend.booking.entity.BookingEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Builder
@Component
public class BookingMapper {

    public BookingResponseDto entityToResponseDto(BookingEntity booking){
        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .slotId(booking.getSlotId())
                .status(booking.getStatus())
                .amount(booking.getAmount())
                .bookedDate(booking.getBookedDate())
                .slotStartTime(booking.getSlotStartTime())
                .slotEndTime(booking.getSlotEndTime())
                .build();

    }

}

