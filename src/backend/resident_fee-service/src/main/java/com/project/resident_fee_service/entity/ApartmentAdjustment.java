package com.project.resident_fee_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
public class ApartmentAdjustment {

    @Column(name = "AdjustmentID")
    public Long adjustmentId;

    @Column(name = "FeeID")
    public Long feeId;

    @Column(name = "AdjustmentAmount")
    public BigDecimal adjustmentAmount;

    @Column(name = "AdjustmentType")
    public String adjustmentType;

    @Column(name = "Reason")
    public String reason;

    @Column(name = "EffectiveDate")
    public LocalDate effectiveDate;

    @Column(name = "ExpiryDate")
    public LocalDate expiryDate;
}
