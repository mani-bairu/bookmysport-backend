package com.bookmysport.backend.venue.repository;

import com.bookmysport.backend.common.enums.SportType;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.common.enums.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<VenueEntity,Long> {

    List<VenueEntity> findByOwner_Id(Long owner_id);

    Optional<VenueEntity> findByIdAndOwner_Id(Long venue_id, Long owner_id);

    Optional<VenueEntity> findByIdAndStatus(Long venue_id, VenueStatus status);


    List<VenueEntity> findByCityAndStatus(String city, VenueStatus Status);

//    List<VenueEntity>  getVenuesBySportType(SportType sportType, VenueStatus Status);
    List<VenueEntity> findDistinctBySportAreas_SportTypeAndStatus(
        SportType sportType,
        VenueStatus status );

    /**
     * Haversine formula — finds APPROVED venues within radiusKm of a lat/lng point.
     */
//    @Query(value = """
//        SELECT v.* FROM VenueEntity v
//        WHERE v.status = 'APPROVED'
//          AND v.city = :city
//        HAVING (
//          6371 * ACOS(
//            COS(RADIANS(:lat)) * COS(RADIANS(v.latitude))
//            * COS(RADIANS(v.longitude) - RADIANS(:lng))
//            + SIN(RADIANS(:lat)) * SIN(RADIANS(v.latitude))
//          )
//        ) <= :radiusKm
//        ORDER BY (
//          6371 * ACOS(
//            COS(RADIANS(:lat)) * COS(RADIANS(v.latitude))
//            * COS(RADIANS(v.longitude) - RADIANS(:lng))
//            + SIN(RADIANS(:lat)) * SIN(RADIANS(v.latitude))
//          )
//        )
//        LIMIT 50
//        """, nativeQuery = true)
//    List<VenueEntity> findNearbyApprovedVenues(
//            @Param("lat") BigDecimal lat,
//            @Param("lng") BigDecimal lng,
//            @Param("city") String city,
//            @Param("radiusKm") double radiusKm);

}
