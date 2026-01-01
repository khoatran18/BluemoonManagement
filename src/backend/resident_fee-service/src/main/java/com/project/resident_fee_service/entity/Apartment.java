package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Apartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ApartmentID")
    private Long apartmentId;

    @Column(name = "Building", length = 30, nullable = false)
    private String building;

    @Column(name = "RoomNumber", length = 6, nullable = false)
    private String roomNumber;

    /**
     * Head resident FK.
     * ON DELETE SET NULL (if the referenced Resident is deleted, this becomes NULL)
     */
    @ManyToOne
    @JoinColumn(
            name = "HeadResidentID",
            foreignKey = @ForeignKey(
                    name = "fk_apartment_head_resident",
                    foreignKeyDefinition = "FOREIGN KEY (HeadResidentID) REFERENCES Resident(ResidentID) ON DELETE SET NULL"
            )
    )
    private Resident headResident;

    @OneToMany(mappedBy = "apartment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Resident> residents;
    /**
     * Fee status rows for this apartment.
     * ApartmentFeeStatus will own the ApartmentID FK (with ON DELETE CASCADE).
     */
    @OneToOne(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentFeeStatus apartmentFeeStatus;

    /**
     * 1 Apartment can have many ApartmentSpecificAdjustments.
     */
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(
//            name = "ApartmentAdjustment",
//            joinColumns = @JoinColumn(name = "ApartmentID")
//    )
//    private Set<ApartmentAdjustment> adjustments = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "Apartment_Adjustment",
            joinColumns = @JoinColumn(name = "ApartmentID"),
            inverseJoinColumns = @JoinColumn(name = "AdjustmentID")
    )
    private List<Adjustment> adjustments = new ArrayList<>();

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
