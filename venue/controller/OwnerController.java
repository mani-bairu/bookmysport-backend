package com.bookmysport.backend.venue.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.venue.dto.requestdto.CreatePricingRuleRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.CreateSportAreaRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.CreateVenueRequestDto;
import com.bookmysport.backend.venue.dto.requestdto.UpdateVenueRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.PricingRuleResponseDto;
import com.bookmysport.backend.venue.dto.responsedto.SportAreaResponseDto;
import com.bookmysport.backend.venue.dto.responsedto.VenueFullDetailsResponseDTo;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.venue.repository.VenueRepository;
import com.bookmysport.backend.venue.service.PricingRuleService;
import com.bookmysport.backend.venue.service.SportAreaService;
import com.bookmysport.backend.venue.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/partner")
@PreAuthorize("hasAuthority('OWNER')")
public class OwnerController {

   private final VenueService venueService;
   private final SportAreaService sportAreaService;
   private final VenueRepository venueRepository;
   private final PricingRuleService pricingRuleService;


   // venue methods - > addvenue, updatevenue , getownerVenue, getOwnervenues

   @PostMapping("/venue")
    public ResponseEntity<ApiResponse<VenueFullDetailsResponseDTo>> addVenue(
            @Valid @RequestBody CreateVenueRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser){

        VenueFullDetailsResponseDTo venue = venueService.createVenue(dto, securityUser.getUser().getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<VenueFullDetailsResponseDTo>builder()
                        .success(true)
                        .message("Venue is Created Successfully")
                        .timestamp(LocalDateTime.now())
                        .data(venue)
                        .build()
                );
    }


    @PutMapping("/venue/{venue_id}")
    public ResponseEntity<ApiResponse<VenueFullDetailsResponseDTo>> updateVenue(
            @Valid
            @PathVariable Long venue_id,
            @RequestBody UpdateVenueRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser){

        VenueFullDetailsResponseDTo updatedVenue = venueService.updateVenue(dto, securityUser.getUser().getId(), venue_id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<VenueFullDetailsResponseDTo>builder()
                        .success(true)
                        .message("Venue is Updated Successfully")
                        .timestamp(LocalDateTime.now())
                        .data(updatedVenue)
                        .build()
                );
   }


    @GetMapping("venue/{venue_id}")
    public ResponseEntity<ApiResponse<VenueFullDetailsResponseDTo>> getOwnerVenue(
            @Valid
            @PathVariable Long venue_id ,
            @AuthenticationPrincipal SecurityUser securityUser){
        VenueFullDetailsResponseDTo ownerVenue = venueService.getOwnerVenue(securityUser.getUser().getId(),venue_id);


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<VenueFullDetailsResponseDTo>builder()
                        .success(true)
                        .message("here is your venue!!")
                        .timestamp(LocalDateTime.now())
                        .data(ownerVenue)
                        .build()
                );
   }

    @GetMapping("/venues")
    public ResponseEntity<ApiResponse<List<VenueFullDetailsResponseDTo>>> getOwnerVenues(
            @Valid @AuthenticationPrincipal SecurityUser securityUser){
        List<VenueFullDetailsResponseDTo> ownerVenues = venueService.getOwnerVenues(securityUser.getUser().getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<VenueFullDetailsResponseDTo>>builder()
                        .success(true)
                        .message("here is your venues!!")
                        .timestamp(LocalDateTime.now())
                        .data(ownerVenues)
                        .build()
                );
    }

    // sportArea methods - > getOwnerSportsAreas, AddSportArea, UpdateSportArea


    @GetMapping("/sportArea/venue/{venue_id}")
    public ResponseEntity<ApiResponse<List<SportAreaResponseDto>>> getOwnerSportArea(
            @Valid @PathVariable Long venue_id, @AuthenticationPrincipal SecurityUser securityUser){
        VenueEntity byOwnerId = venueRepository.findByIdAndOwner_Id(venue_id,securityUser.getUser().getId()).orElseThrow(()-> new ResourseNotFoundException("venue not found"));

//        VenueEntity venue = venueRepository.findById(venue_id).orElseThrow(()-> new ResourseNotFoundException("venue not found"));
        List<SportAreaResponseDto> sportAreas = sportAreaService.getOwnerSportArea(venue_id,securityUser.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<SportAreaResponseDto>>builder()
                        .success(true)
                        .message("here is your sport areas of "+ byOwnerId.getVenueName())
                        .timestamp(LocalDateTime.now())
                        .data(sportAreas)
                        .build()
                );
    }

    // get all sport Areas of owner

//    @GetMapping("/venues/sportArea{venue_id}")
//    public ResponseEntity<ApiResponse<List<SportAreaResponseDto>>> getOwnerSportAreas(
//            @Valid @PathVariable Long venue_id, @AuthenticationPrincipal SecurityUser securityUser){
//        VenueEntity byOwnerId = venueRepository.findByIdAndOwner_Id(venue_id,securityUser.getUser().getId()).orElseThrow(()-> new ResourseNotFoundException("venue not found"));
//
////        VenueEntity venue = venueRepository.findById(venue_id).orElseThrow(()-> new ResourseNotFoundException("venue not found"));
//        List<SportAreaResponseDto> sportAreas = sportAreaService.getSportAres(venue_id);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.<List<SportAreaResponseDto>>builder()
//                        .success(true)
//                        .message("here is your sport areas of "+ byOwnerId.getVenueName())
//                        .timestamp(LocalDateTime.now())
//                        .data(sportAreas)
//                        .build()
//                );
//    }


    @PostMapping("/sportArea/{venue_id}")
    public ResponseEntity<ApiResponse<SportAreaResponseDto>> addSportArea(
            @Valid
            @PathVariable Long venue_id,
            @RequestBody CreateSportAreaRequestDto dto,
            @AuthenticationPrincipal SecurityUser securityUser){
        Long owner_id = securityUser.getUser().getId();

        SportAreaResponseDto sportAreaResponseDto = sportAreaService.addSportArea(dto, venue_id, owner_id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SportAreaResponseDto>builder()
                        .success(true)
                        .message("Sport area is created")
                        .timestamp(LocalDateTime.now())
                        .data(sportAreaResponseDto)
                        .build());
    }


    // Add pricingRule, getPricingRule

    /** POST /api/v1/owner/sport-areas/{areaId}/pricing */
    @PostMapping("venues/sport-areas/{areaId}/pricing")
    public ResponseEntity<ApiResponse<PricingRuleResponseDto>> addPricingRule(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long areaId,
            @Valid @RequestBody CreatePricingRuleRequestDto request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long ownerId = securityUser.getUser().getId();
        PricingRuleResponseDto pricingRuleResponseDto = pricingRuleService.addPricingRule(ownerId, areaId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PricingRuleResponseDto>builder()
                        .success(true)
                        .message("pricing rule add successfully")
                        .timestamp(LocalDateTime.now())
                        .data(pricingRuleResponseDto)
                        .build()
                        );
    }

    /** GET /api/v1/owner/sport-areas/{areaId}/pricing */
    @GetMapping("/sport-areas/{areaId}/pricing")
    public ResponseEntity<ApiResponse<List<PricingRuleResponseDto>>> getPricingRules(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long areaId) {
        Long ownerId = securityUser.getUser().getId();
//        Long ownerId = jwtUtil.extractUserId(auth.substring(7));

        List<PricingRuleResponseDto> pricingRules = pricingRuleService.getPricingRules(ownerId, areaId);
        return ResponseEntity.ok(
                ApiResponse.<List<PricingRuleResponseDto>>builder()
                        .success(true)
                        .message("pricing rule add successfully")
                        .timestamp(LocalDateTime.now())
                        .data(pricingRules)
                        .build()
        );

    }

//    /** DELETE /api/v1/owner/sport-areas/{areaId}/pricing/{ruleId} */
//    @DeleteMapping("/sport-areas/{areaId}/pricing/{ruleId}")
//    public ResponseEntity<ApiResponse<Void>> deletePricingRule(
//            @RequestHeader("Authorization") String auth,
//            @PathVariable Long areaId,
//            @PathVariable Long ruleId) {
//        Long ownerId = jwtUtil.extractUserId(auth.substring(7));
//        pricingRuleService.deletePricingRule(ownerId, areaId, ruleId);
//        return ResponseEntity.ok(ApiResponse.success("Pricing rule deleted", null));
//    }


}
