package com.bookmysport.backend.venue.dto.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVenueRequestDto {
    private String name;
    private String description;
}
