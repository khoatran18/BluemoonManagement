package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Adjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdjustmentID")
    private Long adjustmentId;

    /**
     * Each Adjustment belongs to one Fee.
     * ON DELETE CASCADE (delete adjustments if Fee is removed)
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "FeeID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_adjustment_fee",
                    foreignKeyDefinition = "FOREIGN KEY (FeeID) REFERENCES Fee(FeeID) ON DELETE CASCADE"
            )
    )
    private Fee fee;

    @Column(name = "AdjustmentAmount", precision = 12, scale = 2)
    private BigDecimal adjustmentAmount;

    public enum AdjustmentType {
        increase,
        decrease
    }
    @Column(name = "AdjustmentType", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private AdjustmentType adjustmentType;

    @Column(name = "Reason", length = 300)
    private String reason;

    @Column(name = "EffectiveDate", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "ExpiryDate", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Each Adjustment can have many Apartment-specific Adjustments.
     * ON DELETE CASCADE for these is implemented on the ApartmentSpecificAdjustment side.
     */
    @OneToMany(mappedBy = "adjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApartmentSpecificAdjustment> apartmentSpecificAdjustments;
}
