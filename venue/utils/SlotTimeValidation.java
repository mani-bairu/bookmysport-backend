package com.bookmysport.backend.venue.utils;

import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.venue.dto.requestdto.CreateSportAreaRequestDto;
import lombok.Data;

import java.time.Duration;

public class SlotTimeValidation {

    public static void validateTimes(CreateSportAreaRequestDto req) {
        if (!req.getOpeningTime().isBefore(req.getClosingTime())) {
            throw new BadRequestException("Opening time must be before closing time");
        }
        long totalMinutes = Duration.between(req.getOpeningTime(), req.getClosingTime()).toMinutes();
        if (totalMinutes < req.getDurationMinutes()) {
            throw new BadRequestException(
                    "Slot duration (" + req.getDurationMinutes() + " min) exceeds " +
                            "operating window (" + totalMinutes + " min)");
        }
    }
}
