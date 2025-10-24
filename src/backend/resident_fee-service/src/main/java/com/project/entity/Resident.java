package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Resident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResidentID")
    private Long residentId;

    /**
     * Owner-tenant self reference (OwnerResidentID).
     * If the owner Resident is deleted -> set this OwnerResidentID to NULL.
     */
    @ManyToOne
    @JoinColumn(
            name = "OwnerResidentID",
            foreignKey = @ForeignKey(
                    name = "fk_resident_owner_resident",
                    foreignKeyDefinition = "FOREIGN KEY (OwnerResidentID) REFERENCES Resident(ResidentID) ON DELETE SET NULL"
            )
    )
    private Resident ownerResident;

    /**
     * Resident belongs to an Apartment (owning side).
     * ON DELETE CASCADE: if Apartment is removed, Resident rows are removed by DB.
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "ApartmentID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resident_apartment",
                    foreignKeyDefinition = "FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID) ON DELETE CASCADE"
            )
    )
    private Apartment apartment;

    @Column(name = "FullName", length = 30, nullable = false)
    private String fullName;

    @Column(name = "PhoneNumber", length = 10)
    private String phoneNumber;

    @Column(name = "Email", length = 40)
    private String email;

    /**
     * IsHead (bit)
     */
    @Column(name = "IsHead")
    private Boolean isHead;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
