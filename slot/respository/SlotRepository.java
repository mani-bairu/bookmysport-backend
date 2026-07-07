package com.bookmysport.backend.slot.respository;

import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlotRepository extends JpaRepository<SlotEntity, Long> {

   List<SlotEntity> findBySportAreaIdAndDate(Long sportAreaId, LocalDate date);

   Boolean existsBySportAreaIdAndDate(Long sportAreaId, LocalDate date);

   List<SlotEntity> findByStatusAndLockExpiresAtBefore(
                                          SlotStatus status,
                                          LocalDateTime lockExpiry);



}
