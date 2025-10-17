package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String feeName;

    @Column(precision = 12, scale = 2)
    private BigDecimal feeAmount;

    @Column(length = 4)
    private String applicableMonth;

    private LocalDate effectiveDate;
    private LocalDate expiryDate;

    @Column(length = 8)
    private String status;

    // Getter and Setter

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getFeeName() {
//        return feeName;
//    }
//
//    public void setFeeName(String feeName) {
//        this.feeName = feeName;
//    }
//
//    public BigDecimal getFeeAmount() {
//        return feeAmount;
//    }
//
//    public void setFeeAmount(BigDecimal feeAmount) {
//        this.feeAmount = feeAmount;
//    }
//
//    public String getApplicableMonth() {
//        return applicableMonth;
//    }
//
//    public void setApplicableMonth(String applicableMonth) {
//        this.applicableMonth = applicableMonth;
//    }
//
//    public LocalDate getEffectiveDate() {
//        return effectiveDate;
//    }
//
//    public void setEffectiveDate(LocalDate effectiveDate) {
//        this.effectiveDate = effectiveDate;
//    }
//
//    public LocalDate getExpiryDate() {
//        return expiryDate;
//    }
//
//    public void setExpiryDate(LocalDate expiryDate) {
//        this.expiryDate = expiryDate;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
}
