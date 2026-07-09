package com.bookmysport.backend.user.dto.Response;

import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.common.enums.SportType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBookingsResponseDto {

    private  Long bookingId;

    private LocalDateTime bookingDate;

    private String venueName;

    private String sportAreaName;

    private LocalTime slotStartTime;

    private  LocalTime slotEndTime;

    private LocalDate SlotBookedDate;

    private BigDecimal amountPaid;

    private SportType sportType;

    private BookingStatus bookingStatus;





}
