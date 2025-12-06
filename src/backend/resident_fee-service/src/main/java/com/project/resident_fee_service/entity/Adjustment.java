package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "FeeID",
            nullable = true,
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
    private LocalDate startDate;

    @Column(name = "ExpiryDate", nullable = false)
    private LocalDate endDate;

    /**
     * Many-to-many reverse side.
     * Apartment is owner side.
     */
    @ManyToMany(mappedBy = "adjustments")
    private List<Apartment> apartments = new ArrayList<>();

}

