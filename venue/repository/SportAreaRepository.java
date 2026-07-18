package com.bookmysport.backend.venue.repository;

import com.bookmysport.backend.common.enums.AreaStatus;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SportAreaRepository extends JpaRepository<SportAreaEntity,Long> {


    List<SportAreaEntity> findByVenue_IdAndVenue_Owner_Id(Long venueId, Long ownerId );
    Optional<SportAreaEntity> findByIdAndVenue_Owner_Id(Long areaId, Long ownerId);
    List<SportAreaEntity> findByVenue_IdAndStatus(Long venueid, AreaStatus status);



    /**
     * The core slot-generation query.
     *z
     * Only returns sport areas where:
     *   1. The PARENT VENUE is APPROVED
     *   2. The sport area itself is ACTIVE
     *
     * This prevents generating slots for sport areas
     * that belong to PENDING / SUSPENDED / REJECTED venues.
     */
    @Query("""
        SELECT sa FROM SportAreaEntity sa
        WHERE sa.status = com.bookmysport.backend.common.enums.AreaStatus.ACTIVE
          AND sa.venue.status = com.bookmysport.backend.common.enums.VenueStatus.APPROVED
        """)
    List<SportAreaEntity> findAllEligibleForSlotGeneration();

    /**
     * Same filter but only uninitialized areas — used for logging/monitoring.
     */
    @Query("""
        SELECT sa FROM SportAreaEntity sa
        WHERE sa.status = com.bookmysport.backend.common.enums.AreaStatus.ACTIVE
          AND sa.venue.status = com.bookmysport.backend.common.enums.VenueStatus.APPROVED
          AND sa.slotsInitialized = false
        """)
    List<SportAreaEntity> findNewAreasNeedingInitialization();

//    @Query("""
//    SELECT sa
//    FROM SportAreaEntity sa
//    JOIN sa.venue v
//    WHERE v.status = com.bookmysport.backend.common.enums.VenueStatus.APPROVED
//      AND sa.status = com.bookmysport.backend.common.enums.AreaStatus.ACTIVE
//""")

    @EntityGraph(attributePaths = {"pricingRules", "venue"})
    @Query("""
        SELECT sa
        FROM SportAreaEntity sa
        JOIN sa.venue v
        WHERE v.status = com.bookmysport.backend.common.enums.VenueStatus.APPROVED
        AND sa.status = com.bookmysport.backend.common.enums.AreaStatus.ACTIVE
    """)
    List<SportAreaEntity> findAllValidSportAreas();


}
