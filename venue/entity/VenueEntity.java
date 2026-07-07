package com.bookmysport.backend.venue.entity;

import com.bookmysport.backend.common.Entity.BaseEntity;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.common.enums.VenueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "venues",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id","name"}),
        indexes = {
                @Index(name = "idx_venue_city",columnList = "city"),
                @Index(name = "idx_venue_owner",columnList = "owner_id"),
                @Index(name = "idx_venue_status",columnList = "status")
        }
)
public class VenueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false, length = 150)
    private String venueName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,length = 300)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 150)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column( precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VenueStatus status= VenueStatus.PENDING;

    @OneToMany(mappedBy = "venue",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SportAreaEntity> sportAreas = new ArrayList<>();

}
