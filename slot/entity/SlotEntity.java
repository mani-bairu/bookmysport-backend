package com.bookmysport.backend.slot.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SlotTable",
                indexes = {
                            @Index(name = "idx_SportArea_Date",columnList= "sportAreaId,date"),
                            @Index(name ="idx_status",columnList="status")
                },
                uniqueConstraints = {
                            @UniqueConstraint(columnNames = {"sportAreaId","date","startTime"})
                }
    )


public class SlotEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long venueId;

    private Long sportAreaId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;

    private Long lockedByUser;

    private LocalDateTime lockedAt;

    private LocalDateTime lockExpiresAt;

//    private Boolean isSlotsInitialized = false;

    private BigDecimal price;

    @Version
    private Long version;
}
