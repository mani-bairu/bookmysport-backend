package com.bookmysport.backend.booking.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import com.bookmysport.backend.common.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long slotId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    // PENDING_PAYMENT, CONFIRMED, CANCELLED

    private BigDecimal amount;

    private LocalDate bookedDate;

    private LocalTime slotStartTime;

    private LocalTime slotEndTime;

}
