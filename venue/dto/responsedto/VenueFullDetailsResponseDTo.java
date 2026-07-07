package com.bookmysport.backend.venue.dto.responsedto;

import com.bookmysport.backend.common.enums.SportType;
import com.bookmysport.backend.common.enums.VenueStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VenueFullDetailsResponseDTo {

    private Long          id;
    private String        name;
    private String        description;
    private String        address;
    private String        city;
    private String        state;
    private String        pincode;
    private VenueStatus status;
    private LocalDateTime createdAt;
    private List<SportAreaResponseDto> sportAreas;
    private List<SportType> sportTypes;
}
