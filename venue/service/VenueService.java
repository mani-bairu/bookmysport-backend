package com.bookmysport.backend.venue.service;

import com.bookmysport.backend.common.enums.SportType;
import com.bookmysport.backend.common.enums.VenueStatus;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.repository.UserRepository;
import com.bookmysport.backend.venue.dto.requestdto.CreateVenueRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.UpdateVenueRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.VenueFullDetailsResponseDTo;
import com.bookmysport.backend.venue.dto.responsedto.VenueSummeryResponseDto;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.venue.mapper.VenueMapper;
import com.bookmysport.backend.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;




@Service
@Transactional
public class VenueService {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    public VenueService(UserRepository userRepository,
                        VenueRepository venueRepository) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }


    // Owner Access methods

    public VenueFullDetailsResponseDTo createVenue(CreateVenueRequestDto dto, Long ownerId){

       UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(()->new ResourseNotFoundException("user not found"));

        VenueEntity entity = VenueMapper.toEntity(dto, owner);

        VenueEntity save = venueRepository.save(entity);

        VenueFullDetailsResponseDTo responseDto = VenueMapper.toResponseDto(save);

        return responseDto;
    }

    public VenueFullDetailsResponseDTo updateVenue(UpdateVenueRequestDto dto, Long ownerId,Long venueId){

        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
                .orElseThrow(()-> new ResourseNotFoundException("venue not found by this user"));

        VenueMapper.updateVenueResponseDTo(dto,venue);

          return VenueMapper.toResponseDto(venue);

    }

    @Transactional(readOnly = true)
    public List<VenueFullDetailsResponseDTo> getOwnerVenues(Long ownerId) {
        List<VenueEntity> ownerVenues = venueRepository.findByOwner_Id(ownerId);

        if (ownerVenues.isEmpty()) {
            throw new ResourseNotFoundException(
                    "No venues found");
        }
        return ownerVenues.stream()
                .map(VenueMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueFullDetailsResponseDTo getOwnerVenue(Long ownerId, Long venueId){
        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
                .orElseThrow(()-> new ResourseNotFoundException("Venue not found"));
        return  VenueMapper.toResponseDto(venue);
    }

    // Public Access methods


    @Transactional(readOnly = true)
    public List<VenueSummeryResponseDto> getVenues(String city){
        List<VenueEntity> venues = venueRepository.findByCityAndStatus(city, VenueStatus.APPROVED);
        if(venues.isEmpty()){
            throw new ResourseNotFoundException("Venues Not Found");
        }
        return venues.stream()
                .map(VenueMapper::toSummeryResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueFullDetailsResponseDTo getVenue(Long venueId){
        VenueEntity byIdAndOwnerId = venueRepository.findByIdAndStatus(venueId,VenueStatus.APPROVED)
                .orElseThrow(()-> new ResourseNotFoundException("Venue not found"));
        return  VenueMapper.toResponseDto(byIdAndOwnerId);
    }

    @Transactional(readOnly = true)
    public List<VenueSummeryResponseDto> getVenueBySportType(SportType sport) {
        List<VenueEntity> venues = venueRepository.findDistinctBySportAreas_SportTypeAndStatus(sport,VenueStatus.APPROVED);
        if (venues.isEmpty()) {
            throw new ResourseNotFoundException("Venues Not Found");
        }
        return venues.stream()
                .map(VenueMapper::toSummeryResponseDto)
                .toList();

    }


}
