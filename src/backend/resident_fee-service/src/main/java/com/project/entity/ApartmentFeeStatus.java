package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "apartment_fee_status")
public class ApartmentFeeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    // Nhiều-nhiều: mỗi apartment có nhiều fee (paid/unpaid)
    @ManyToMany
    @JoinTable(
            name = "unpaid_fees",
            joinColumns = @JoinColumn(name = "apartment_id"),
            inverseJoinColumns = @JoinColumn(name = "fee_id")
    )
    private Set<Fee> unpaidFees = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "paid_fees",
            joinColumns = @JoinColumn(name = "apartment_id"),
            inverseJoinColumns = @JoinColumn(name = "fee_id")
    )
    private Set<Fee> paidFees = new HashSet<>();

    @Column(precision = 12, scale = 2)
    private BigDecimal totalFee;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalPaid;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance;

    private LocalDate updatedAt;

    // Getter and Setter

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Apartment getApartment() {
//        return apartment;
//    }
//
//    public void setApartment(Apartment apartment) {
//        this.apartment = apartment;
//    }
//
//    public Set<Fee> getUnpaidFees() {
//        return unpaidFees;
//    }
//
//    public void setUnpaidFees(Set<Fee> unpaidFees) {
//        this.unpaidFees = unpaidFees;
//    }
//
//    public Set<Fee> getPaidFees() {
//        return paidFees;
//    }
//
//    public void setPaidFees(Set<Fee> paidFees) {
//        this.paidFees = paidFees;
//    }
//
//    public BigDecimal getTotalFee() {
//        return totalFee;
//    }
//
//    public void setTotalFee(BigDecimal totalFee) {
//        this.totalFee = totalFee;
//    }
//
//    public BigDecimal getTotalPaid() {
//        return totalPaid;
//    }
//
//    public void setTotalPaid(BigDecimal totalPaid) {
//        this.totalPaid = totalPaid;
//    }
//
//    public BigDecimal getBalance() {
//        return balance;
//    }
//
//    public void setBalance(BigDecimal balance) {
//        this.balance = balance;
//    }
//
//    public LocalDate getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDate updatedAt) {
//        this.updatedAt = updatedAt;
//    }
}
