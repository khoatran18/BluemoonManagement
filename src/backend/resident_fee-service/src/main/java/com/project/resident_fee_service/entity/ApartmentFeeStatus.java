package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "ApartmentFeeStatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentFeeStatus {

    /**
     * Shared primary key with Apartment.
     * Acts as both PK and FK to Apartment(ApartmentID).
     * Enforces 1:1 relationship — one status record per apartment.
     */
    @Id
    @OneToOne(optional = false)
    @JoinColumn(
            name = "ApartmentID",
            referencedColumnName = "ApartmentID",
            foreignKey = @ForeignKey(
                    name = "fk_afs_apartment",
                    foreignKeyDefinition = "FOREIGN KEY (ApartmentID) REFERENCES Apartment(ApartmentID) ON DELETE CASCADE"
            )
    )
    private Apartment apartment;

    /**
     * Fee relationship.
     * ON DELETE RESTRICT (cannot delete a Fee if statuses exist)
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "FeeID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_afs_fee",
                    foreignKeyDefinition = "FOREIGN KEY (FeeID) REFERENCES Fee(FeeID) ON DELETE RESTRICT"
            )
    )
    private Fee fee;
    @ManyToMany
    @JoinTable(
            name = "ApartmentFeeStatus_UnpaidFees",
            joinColumns = @JoinColumn(name = "ApartmentID"),
            inverseJoinColumns = @JoinColumn(name = "FeeID")
    )
    private Set<Fee> unpaidFeeList;

    @ManyToMany
    @JoinTable(
            name = "ApartmentFeeStatus_PaidFees",
            joinColumns = @JoinColumn(name = "ApartmentID"),
            inverseJoinColumns = @JoinColumn(name = "FeeID")
    )
    private Set<Fee> paidFeeList;

    @ManyToMany
    @JoinTable(
            name = "ApartmentFeeStatus_Adjustments",
            joinColumns = @JoinColumn(name = "ApartmentID"),
            inverseJoinColumns = @JoinColumn(name = "AdjustmentID")
    )
    private Set<Adjustment> adjustmentList;

    @ManyToMany
    @JoinTable(
            name = "ApartmentFeeStatus_ExtraAdjustments",
            joinColumns = @JoinColumn(name = "ApartmentID"),
            inverseJoinColumns = @JoinColumn(name = "AdjustmentID")
    )
    private Set<Adjustment> extraAdjustmentList;

    @Column(name = "AmountDue", precision = 12, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "AmountPaid", precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "Balance", precision = 12, scale = 2)
    private BigDecimal balance;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
