package com.bookmysport.backend.user.repository;

import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.user.dto.Response.UserBookingsResponseDto;
import com.bookmysport.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("""
            SELECT new com.bookmysport.backend.user.dto.Response.UserBookingsResponseDto(
            b.id,
            b.createdAt,
            v.venueName,
            sa.sportAreaName,
            b.slotStartTime,
            b.slotEndTime,
            b.bookedDate,
            b.amount,
            sa.sportType,
            b.status
            )
            FROM BookingEntity b
            JOIN SlotEntity s
                ON b.slotId = s.id
            JOIN VenueEntity v
                ON s.venueId = v.id
            JOIN SportAreaEntity sa
                ON s.sportAreaId = sa.id
            WHERE b.userId = :userId
            AND b.status =:status
            ORDER BY b.bookedDate DESC , b.slotStartTime ASC
            """)
    List<UserBookingsResponseDto> userBookingList(
            @Param("userId") Long userId,
            @Param("status")BookingStatus status
            );


}
