package com.bookmysport.backend.venue.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import com.bookmysport.backend.common.enums.AreaStatus;
import com.bookmysport.backend.common.enums.SportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sport_areas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id","sport_area_name"}),
        indexes = {
                @Index(name = "idx_sport_area_venue_id",columnList = "venue_id"),
                @Index(name = "idx_sport_area_sport_type", columnList = "sport_type"),
                @Index(name = "idx_sport_area_status", columnList = "status")
        }
)
public class SportAreaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id",nullable = false)
    private VenueEntity venue;

    @Column(name = "sport_area_name",nullable = false, length = 150)
    private String sportAreaName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false, length = 50)
    private SportType sportType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    // ── Slot generation config ────────────────────────────────
    // Owner sets these once. Scheduler uses them to generate slots.

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 60;

    /**
     * FALSE  → area is new, scheduler will generate full 30 days on next run
     * TRUE   → area already has slots, scheduler adds only the rolling +30 day
     */
    @Column(name = "slots_initialized", nullable = false)
    @Builder.Default
    private Boolean slotsInitialized = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AreaStatus status = AreaStatus.INACTIVE;

    // Pricing rules are owned here in venue module
    @OneToMany(mappedBy = "sportArea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PricingRuleEntity> pricingRules = new ArrayList<>();

}
