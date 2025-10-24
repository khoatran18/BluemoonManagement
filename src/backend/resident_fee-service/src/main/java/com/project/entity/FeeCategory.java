package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "FeeCategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FeeCategoryID")
    private Long feeCategoryId;

    /**
     * Each FeeCategory belongs to one FeeType.
     * ON DELETE CASCADE ensures categories are deleted if the FeeType is removed.
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "FeeTypeID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_feecategory_feetype",
                    foreignKeyDefinition = "FOREIGN KEY (FeeTypeID) REFERENCES FeeType(FeeTypeID) ON DELETE CASCADE"
            )
    )
    private FeeType feeType;

    @Column(name = "FeeCategoryDescription", length = 100)
    private String feeCategoryDescription;

    @Column(name = "FeeCategoryName", length = 30, nullable = false)
    private String feeCategoryName;

    /**
     * One FeeCategory subcategorizes many Fees.
     * DB-level ON DELETE CASCADE on Fee.FeeCategoryID.
     */
    @OneToMany(mappedBy = "feeCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fee> fees;
}
