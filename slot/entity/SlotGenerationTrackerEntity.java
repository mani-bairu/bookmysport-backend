package com.bookmysport.backend.slot.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SlotGenerationTracker",
                indexes = {
                            @Index(name = "idx_sportArea_tracker",columnList = "sportAreaId")
                })
public class SlotGenerationTrackerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sportAreaId;

    // 🔥 CRITICAL FIELD
    // instead of "boolean generated", we track until where we generated
    private LocalDate generatedUntilDate;

    private LocalDateTime lastRunAt;
}
