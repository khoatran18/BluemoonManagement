package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FeeID")
    private Long feeId;

    /**
     * FeeType relationship.
     * ON DELETE CASCADE (if FeeType removed, Fees removed).
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "FeeTypeID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_fee_feetype",
                    foreignKeyDefinition = "FOREIGN KEY (FeeTypeID) REFERENCES FeeType(FeeTypeID) ON DELETE CASCADE"
            )
    )
    private FeeType feeType;

    /**
     * FeeCategory relationship.
     * ON DELETE CASCADE (if FeeCategory removed, Fees removed).
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "FeeCategoryID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_fee_feecategory",
                    foreignKeyDefinition = "FOREIGN KEY (FeeCategoryID) REFERENCES FeeCategory(FeeCategoryID) ON DELETE CASCADE"
            )
    )
    private FeeCategory feeCategory;

    @Column(name = "FeeName", length = 50, nullable = false)
    private String feeName;

    @Column(name = "FeeDescription", length = 300, nullable = false)
    private String feeDescription;

    @Column(name = "ApplicableMonth", length = 4)
    private String applicableMonth;

    @Column(name = "Amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    public enum FeeStatus {
        DRAFT,
        ACTIVE,
        CLOSED,
        ARCHIVED
    }

    @Column(name = "Status", length = 8)
    @Enumerated(EnumType.STRING)
    private FeeStatus status;

    /**
     * Fee has many ApartmentFeeStatus rows.
     * DB ON DELETE RESTRICT prevents deleting a Fee while status records exist.
     */
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApartmentFeeStatus> apartmentFeeStatuses;

    /**
     * Fee has many Adjustments.
     * ON DELETE CASCADE on Adjustment.FeeID.
     */
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adjustment> adjustments;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
