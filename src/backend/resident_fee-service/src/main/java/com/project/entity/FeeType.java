package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "FeeType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FeeTypeID")
    private Long feeTypeId;

    public enum FeeTypeName {
        OBLIGATORY,
        VOLUNTARY,
        IMPROMPTU
    }

    @Column(name = "FeeTypeName", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private FeeTypeName feeTypeName;

    /**
     * One FeeType has many FeeCategory records.
     * Cascade persist to keep the association in sync.
     * DB-level ON DELETE CASCADE defined on FeeCategory.FeeTypeID FK.
     */
    @OneToMany(mappedBy = "feeType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeCategory> feeCategories;

    /**
     * One FeeType directly categorizes many Fees.
     * DB-level ON DELETE CASCADE on Fee.FeeTypeID FK.
     */
    @OneToMany(mappedBy = "feeType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fee> fees;
}
