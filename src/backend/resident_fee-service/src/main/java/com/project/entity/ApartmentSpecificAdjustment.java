package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ApartmentSpecificAdjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentSpecificAdjustment {

    /**
     * Composite key: (ApartmentID, AdjustmentID)
     * Both columns are also foreign keys.
     */
    @EmbeddedId
    private ApartmentSpecificAdjustmentId id;

    /**
     * Apartment relationship.
     * ON DELETE CASCADE: if Apartment deleted → related links removed.
     */
    @MapsId("apartmentId")
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "ApartmentID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_asa_apartment",
                    foreignKeyDefinition = "FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID) ON DELETE CASCADE"
            )
    )
    private Apartment apartment;

    /**
     * Adjustment relationship.
     * ON DELETE CASCADE: if Adjustment deleted → related links removed.
     */
    @MapsId("adjustmentId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "AdjustmentID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_asa_adjustment",
                    foreignKeyDefinition = "FOREIGN KEY (AdjustmentID) REFERENCES Adjustment(AdjustmentID) ON DELETE CASCADE"
            )
    )
    private Adjustment adjustment;
}
