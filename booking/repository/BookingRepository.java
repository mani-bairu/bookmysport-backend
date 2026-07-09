package com.bookmysport.backend.booking.repository;

import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.common.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

    BookingEntity findBySlotIdAndStatus(Long slotId, BookingStatus status);

    List<BookingEntity> findByUserId(Long id);
}
