package com.bookmysport.backend.venue.repository;

import com.bookmysport.backend.venue.entity.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRuleEntity,Long> {


    /**
     * Returns all active pricing rules for a sport area on a specific date.
     * A rule is active when validFrom <= date AND (validTo is null OR validTo >= date).
     * Used by SlotGenerationService to resolve price for each slot.
     */
    @Query("""
        SELECT pr FROM PricingRuleEntity pr
        WHERE pr.sportArea.id = :areaId
          AND pr.validFrom <= :date
          AND (pr.validTo IS NULL OR pr.validTo >= :date)
        """)
    List<PricingRuleEntity> findActiveRulesForAreaOnDate(
            @Param("areaId") Long areaId,
            @Param("date") LocalDate date);

    List<PricingRuleEntity> findBySportArea_Id(Long sportAreaId);

    void deleteBySportArea_Id(Long sportAreaId);

}
