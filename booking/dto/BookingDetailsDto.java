package com.bookmysport.backend.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailsDto {


    private String userEmail;
    private String userName;
    private String venueName;

    private String sportAreaName;

    private LocalDate bookedDate;

    private BigDecimal slotPrice;

    private LocalTime slotStartTime;

    private LocalTime slotEndTime;

}
