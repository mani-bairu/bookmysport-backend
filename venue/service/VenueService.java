package com.bookmysport.backend.venue.service;

import com.bookmysport.backend.common.enums.AreaStatus;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class VenueService {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public VenueService(UserRepository userRepository,
                        VenueRepository venueRepository,
                        StringRedisTemplate redisTemplate,
                        ObjectMapper objectMapper) {

        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    // Owner Access methods

    public VenueFullDetailsResponseDTo createVenue(CreateVenueRequestDto dto, Long ownerId){

       UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(()->new ResourseNotFoundException("user not found"));

        VenueEntity entity = VenueMapper.toEntity(dto, owner);

        VenueEntity save = venueRepository.save(entity);

        // Delete cache for that city — so next request fetches fresh data
        String key = "venues:city:" + dto.getCity();
        redisTemplate.delete(key);

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


//    get venues by cities
    @Transactional(readOnly = true)
    public List<VenueSummeryResponseDto> getVenues(String city){

        String key = "venues:city:"+city;
        try {
            String cached= redisTemplate.opsForValue().get(key);
            if(cached!=null){
                System.out.println("Venues data loaded from redis cache");
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class,VenueSummeryResponseDto.class));
            }

        }catch (Exception e){

        System.out.println("Cache read error: " + e.getMessage());

        }
        List<VenueEntity> venues = venueRepository.findByCityAndStatus(city, VenueStatus.APPROVED);
        if(venues.isEmpty()){
            throw new ResourseNotFoundException("Venues Not Found");
        }
        System.out.println("Venues data loaded from Data-base");

        List<VenueSummeryResponseDto> venuesList = venues.stream()
                .map(VenueMapper::toSummeryResponseDto)
                .toList();
        try{
            String json = objectMapper.writeValueAsString(venuesList);
            redisTemplate.opsForValue().set(key,json, 1, TimeUnit.DAYS);

        } catch (Exception e) {
            System.out.println("Cache write error: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return venuesList;
    }

    @Transactional(readOnly = true)
    public VenueFullDetailsResponseDTo getVenue(Long venueId){

        String key = "venue:sportAreas:" + venueId;
        try{
            String cached = redisTemplate.opsForValue().get(key);
            if(cached!=null){
                System.out.println("venue and SportArea details loaded from redis cache");
                return objectMapper.readValue(cached,objectMapper.getTypeFactory().constructType(VenueFullDetailsResponseDTo.class));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        VenueEntity byIdAndOwnerId = venueRepository.findByIdAndStatus(venueId,VenueStatus.APPROVED)
                .orElseThrow(()-> new ResourseNotFoundException("Venue not found"));
        VenueFullDetailsResponseDTo responseDto = VenueMapper.toResponseDto(byIdAndOwnerId);

        System.out.println("venue and SportArea details loaded from DataBase");
        try {
            String json = objectMapper.writeValueAsString(responseDto);
            redisTemplate.opsForValue().set(key,json,1,TimeUnit.DAYS);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return responseDto;
    }

    @Transactional(readOnly = true)
    public List<VenueSummeryResponseDto> getVenuesByCityAndSportType(String city ,SportType sportType) {
        List<VenueEntity> venues = venueRepository.
                getVenuesByCityAndSportType(city,
                                            sportType,
                                            VenueStatus.APPROVED,
                                            AreaStatus.ACTIVE);

        if (venues.isEmpty()) {
            throw new ResourseNotFoundException("Venues Not Found");
        }
        return venues.stream()
                .map(VenueMapper::toSummeryResponseDto)
                .toList();

    }


}
