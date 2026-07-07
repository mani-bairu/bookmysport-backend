package com.bookmysport.backend.venue.controller;

import com.bookmysport.backend.common.ResponseApiDto.ApiResponse;
import com.bookmysport.backend.common.enums.SportType;
import com.bookmysport.backend.venue.dto.responsedto.SportAreaResponseDto;
import com.bookmysport.backend.venue.dto.responsedto.VenueFullDetailsResponseDTo;
import com.bookmysport.backend.venue.dto.responsedto.VenueSummeryResponseDto;
import com.bookmysport.backend.venue.service.SportAreaService;
import com.bookmysport.backend.venue.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/venues")
public class PublicController {

    private final VenueService venueService;
    private final SportAreaService sportAreaService;

    /**
     * Get venues by city
     *
     * Example:
     * /api/v1/venues?city=Penang
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueSummeryResponseDto>>> getVenues(
            @RequestParam String city
    ) {

        List<VenueSummeryResponseDto> response =
                venueService.getVenues(city);

        return ResponseEntity.ok(
                ApiResponse.<List<VenueSummeryResponseDto>>builder()
                        .success(true)
                        .message("Venues fetched successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

    /**
     * Get single venue details
     */
    @GetMapping("/{venueId}")
    public ResponseEntity<ApiResponse<VenueFullDetailsResponseDTo>> getVenue(
            @PathVariable Long venueId
    ) {

        VenueFullDetailsResponseDTo response =
                venueService.getVenue(venueId);

        return ResponseEntity.ok(
                ApiResponse.<VenueFullDetailsResponseDTo>builder()
                        .success(true)
                        .message("Venue fetched successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

    /**
     * Get venues by sport type
     *
     * Example:
     * /api/v1/venues/search?sportType=CRICKET
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VenueSummeryResponseDto>>> getVenuesBySport(
            @RequestParam SportType sportType
    ) {

        List<VenueSummeryResponseDto> response =
                venueService.getVenueBySportType(sportType);

        return ResponseEntity.ok(
                ApiResponse.<List<VenueSummeryResponseDto>>builder()
                        .success(true)
                        .message("Venues fetched successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

    @GetMapping("sportAres/{venueId}")
    public ResponseEntity<ApiResponse<List<SportAreaResponseDto>>> getVenueSportAres(
            @PathVariable Long venueId
    ) {

        List<SportAreaResponseDto> sportAreas = sportAreaService.getSportAres(venueId);

        return ResponseEntity.ok(
                ApiResponse.<List<SportAreaResponseDto>>builder()
                        .success(true)
                        .message("Sport Areas fetched successfully")
                        .timestamp(LocalDateTime.now())
                        .data(sportAreas)
                        .build()
        );
    }
}
