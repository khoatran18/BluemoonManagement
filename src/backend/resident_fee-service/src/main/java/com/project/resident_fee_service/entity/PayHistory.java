package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PayHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PayHistoryID")
    private Long payHistoryID;

    @Column(name = "ApartmentID")
    private Long apartmentID;

    @Column(name = "FeeID")
    private Long feeID;

    @Column(name = "PayDateTime")
    private LocalDate payDateTime;

    @Column(name = "PayAmount")
    private BigDecimal payAmount;

    @Column(name = "PayNote")
    private String payNote;

}
