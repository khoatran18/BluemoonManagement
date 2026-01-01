package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "AdjustmentBalance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdjustmentBalanceID")
    private Long adjustmentBalanceID;

    @Column(name = "ApartmentID")
    private Long apartmentID;

    @Column(name = "FeeID")
    private Long feeID;

    @Column(name = "AdjustmentID")
    private Long adjustmentID;

    @Column(name = "OldBalance")
    private BigDecimal oldBalance;

    @Column(name = "NewBalance")
    private BigDecimal newBalance;

    @Column(name = "PayDateTime")
    private LocalDate payDateTime;

    @Column(name = "AdjustmentTime")
    private LocalDate adjustmentTime;

    @Column(name = "AdjustmentBalanceNote")
    private String adjustmentBalanceNote;

}
