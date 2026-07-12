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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class SportAreaService {

    private final VenueRepository venueRepository;
    private final SportAreaRepository sportAreaRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


//    Owner Access methods

    public SportAreaResponseDto addSportArea(CreateSportAreaRequestDto dto, Long venueId, Long ownerId){

        VenueEntity venue = venueRepository.findByIdAndOwner_Id(venueId, ownerId)
                .orElseThrow(()-> new ResourseNotFoundException("Not venue found"));

        SlotTimeValidation.validateTimes(dto);

        SportAreaEntity entity = SportAreaMapper.toEntity(dto, venue);
        entity.setSlotsInitialized(false);
        SportAreaEntity save = sportAreaRepository.save(entity);

        String key = "venue:sportAreas:" + venue.getId();
        redisTemplate.delete(key);

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
    public List<SportAreaResponseDto> getSportAres(Long venueId) {

        System.out.println("fetching sport areas");

        String key = "venue:sportAreas:" + venueId;
       try{
           String cached = redisTemplate.opsForValue().get(key);
           if(cached != null) {
               System.out.println("sportAreas loaded from redis cache");
               return objectMapper.readValue(cached,
                       objectMapper.getTypeFactory()
                               .constructCollectionType(List.class, SportAreaResponseDto.class));


           }
       } catch (Exception e) {
           throw new RuntimeException(e);
       }

        List<SportAreaEntity> spotAres = sportAreaRepository.findByVenue_IdAndStatus(venueId, AreaStatus.ACTIVE);
        System.out.println("sportAreas loaded from data base");
        List<SportAreaResponseDto> sportAreaslist = spotAres.stream()
                .map(SportAreaMapper::toResponseDto)
                .toList();
        try{
            String json = objectMapper.writeValueAsString(sportAreaslist);
            redisTemplate.opsForValue().set(key,json,1, TimeUnit.DAYS);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sportAreaslist;


    }

}