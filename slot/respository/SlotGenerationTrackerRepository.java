package com.bookmysport.backend.slot.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookmysport.backend.slot.entity.SlotGenerationTrackerEntity;

import java.util.Optional;

public interface SlotGenerationTrackerRepository extends JpaRepository<com.bookmysport.backend.slot.entity.SlotGenerationTrackerEntity,Long> {

   Optional<SlotGenerationTrackerEntity> findBySportAreaId(Long sportAreaId);
}
