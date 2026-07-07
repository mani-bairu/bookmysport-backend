package com.bookmysport.backend.venue.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import com.bookmysport.backend.common.enums.DayType;
import com.bookmysport.backend.common.enums.TimeBand;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "pricing_rules",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"sport_area_id", "day_type", "time_band", "valid_from"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRuleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_area_id", nullable = false)
    private SportAreaEntity sportArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false)
    private DayType dayType;           // WEEKDAY | WEEKEND | HOLIDAY

    @Enumerated(EnumType.STRING)
    @Column(name = "time_band", nullable = false)
    private TimeBand timeBand;         // MORNING | EVENING | NIGHT

    @Column(name = "band_start", nullable = false)
    private LocalTime bandStart;       // e.g. 06:00

    @Column(name = "band_end", nullable = false)
    private LocalTime bandEnd;         // e.g. 12:00

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;          // e.g. 500.00

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;         // null = no expiry


}
