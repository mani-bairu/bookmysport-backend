package com.bookmysport.backend.venue.mapper;

import com.bookmysport.backend.common.enums.SportType;
import com.bookmysport.backend.common.enums.VenueStatus;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.venue.dto.requestdto.CreateVenueRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.UpdateVenueRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.SportAreaResponseDto;
import com.bookmysport.backend.venue.dto.responsedto.VenueFullDetailsResponseDTo;
import com.bookmysport.backend.venue.dto.responsedto.VenueSummeryResponseDto;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.venue.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class VenueMapper {

//    @Autowired
//    VenueRepository venueRepository;

    public static VenueEntity toEntity(CreateVenueRequestDto venueDto, UserEntity owner){


        VenueEntity venue = new VenueEntity();
        venue.setOwner(owner);
        venue.setVenueName(venueDto.getName());
        venue.setDescription(venueDto.getDescription());
        venue.setAddress(venueDto.getAddress());
        venue.setCity(venueDto.getCity());
        venue.setState(venueDto.getState());
        venue.setPincode(venueDto.getPincode());
        venue.setPhoneNumber(venueDto.getPhoneNumber());
        venue.setStatus(VenueStatus.PENDING);

        return venue;
    }

    public static VenueFullDetailsResponseDTo toResponseDto(VenueEntity venue){


        List<SportAreaResponseDto> sportAreas = venue.getSportAreas()
                .stream()
                .map(SportAreaMapper::toResponseDto)
                .toList();

        List<SportType> sportTypes = venue.getSportAreas()
                .stream()
                .map(SportAreaEntity::getSportType)
                .distinct()
                .toList();

        VenueFullDetailsResponseDTo venueResponse = VenueFullDetailsResponseDTo.builder()

                .id(venue.getId())
                .name(venue.getVenueName())
                .description(venue.getDescription())
                .address(venue.getAddress())
                .city(venue.getCity())
                .state(venue.getState())
                .pincode(venue.getPincode())
                .status(venue.getStatus())
                .createdAt(venue.getCreatedAt())
                .sportAreas(sportAreas)
                .sportTypes(sportTypes)
                .build();
        return venueResponse;
    }

    public static VenueSummeryResponseDto toSummeryResponseDto(VenueEntity venue){

        List<SportType> sportTypes = venue.getSportAreas()
                .stream()
                .map(SportAreaEntity::getSportType)
                .distinct()
                .toList();

        return VenueSummeryResponseDto.builder()
                .id(venue.getId())
                .name(venue.getVenueName())
                .city(venue.getCity())
                .sports(sportTypes)
                .build();

    }

    public static void updateVenueResponseDTo (UpdateVenueRequestDto dto, VenueEntity venue){
        if (dto.getName() != null) {
            venue.setVenueName(dto.getName());
        }

        if (dto.getDescription() != null) {
            venue.setDescription(dto.getDescription());
        }
    }
}
