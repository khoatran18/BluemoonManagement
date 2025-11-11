package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    /**
     * One Apartment has many Residents (occupants).
     * mappedBy = "apartment" (Resident owns the FK ApartmentID).
     * JPA cascade here to allow in-memory cascade when removing the apartment.
     * DB-level ON DELETE CASCADE is defined on Resident.ApartmentID FK.
     */
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
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
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApartmentSpecificAdjustment> apartmentSpecificAdjustments = new HashSet<>();

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
