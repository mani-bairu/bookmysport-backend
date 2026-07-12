package com.bookmysport.backend.booking.repository;

import com.bookmysport.backend.booking.dto.BookingDetailsDto;
import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.common.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

//    BookingEntity findBySlotIdAndStatus(Long slotId, BookingStatus status);

    List<BookingEntity> findByUserId(Long id);

    Optional<BookingEntity> findBySlotIdAndStatus(Long slotId, BookingStatus status);


    @Query("""
            select new com.bookmysport.backend.booking.dto.BookingDetailsDto(
            b.userId,
            b.slotId,
            b.id,
            u.email,
            u.name,
            v.venueName,
            sa.sportAreaName,
            b.bookedDate,
            b.amount,
            b.slotStartTime,
            b.slotEndTime
            )
            FROM BookingEntity b
            JOIN UserEntity u
                ON b.userId = u.id
            JOIN SlotEntity s
                ON b.slotId = s.id
            JOIN VenueEntity v
                ON s.venueId = v.id
            JOIN SportAreaEntity sa
                ON s.sportAreaId = sa.id
            WHERE b.id = :bookingId
            
            """)
    BookingDetailsDto userBookedDetails(@Param("bookingId") Long bookingId);
}
