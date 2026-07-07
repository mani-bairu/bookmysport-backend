package com.bookmysport.backend.venue.service;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.common.enums.AreaStatus;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.user.entity.UserEntity;

import com.bookmysport.backend.venue.dto.requestdto.CreateSportAreaRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.UpdateSportAreaRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.SportAreaResponseDto;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.venue.mapper.SportAreaMapper;
import com.bookmysport.backend.venue.repository.SportAreaRepository;
import com.bookmysport.backend.venue.repository.VenueRepository;
import com.bookmysport.backend.venue.utils.SlotTimeValidation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SportAreaService {

    private final VenueRepository venueRepository;
    private final SportAreaRepository sportAreaRepository;


//    Owner Access methods

    public SportAreaResponseDto addSportArea(CreateSportAreaRequestDto dto, Long venueId, Long ownerId){

        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
                .orElseThrow(()-> new ResourseNotFoundException("Not venue found"));

        SlotTimeValidation.validateTimes(dto);

        SportAreaEntity entity = SportAreaMapper.toEntity(dto, venue);
        entity.setSlotsInitialized(false);
        SportAreaEntity save = sportAreaRepository.save(entity);
        return SportAreaMapper.toResponseDto(save);

    }

//    public SportAreaResponseDto updateSportArea(UpdateSportAreaRequestDto dto, Long venueId, Long ownerId){
//        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
//                .orElseThrow(()-> new ResourseNotFoundException("Not venue found"));
//    }

    public List<SportAreaResponseDto> getOwnerSportArea(Long venueId, Long ownerId){
        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
                .orElseThrow(()-> new ResourseNotFoundException("Venue Not found with this user name!!"));

        List<SportAreaEntity> spotAres = sportAreaRepository.findByVenue_IdAndVenue_Owner_Id(venueId, ownerId);
        return spotAres.stream()
                .map(SportAreaMapper::toResponseDto)
                .toList();

    }

//    public access

    @Transactional(readOnly = true)
    public List<SportAreaResponseDto> getSportAres(Long venueId){

        List<SportAreaEntity> spotAres = sportAreaRepository.findByVenue_IdAndStatus(venueId, AreaStatus.ACTIVE);
        return spotAres.stream()
                .map(SportAreaMapper::toResponseDto)
                .toList();

    }

}