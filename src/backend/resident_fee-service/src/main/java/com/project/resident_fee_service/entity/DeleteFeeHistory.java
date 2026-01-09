package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "DeleteFeeHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteFeeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HistoryID")
    private Long historyId;

    @Column(name = "FeeID", nullable = false)
    private Long feeId;

    @Column(name = "FeeTypeID", nullable = false)
    private Long feeTypeId;

    @Column(name = "FeeCategoryID", nullable = false)
    private Long feeCategoryId;

    @Column(name = "FeeName", length = 50, nullable = false)
    private String feeName;

    @Column(name = "FeeDescription", length = 300, nullable = false)
    private String feeDescription;

    @Column(name = "ApplicableMonth", length = 20)
    private String applicableMonth;

    @Column(name = "Amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Status", length = 8)
    @Enumerated(EnumType.STRING)
    private Fee.FeeStatus status;

    @Column(name = "DeletedAt", nullable = false)
    private LocalDateTime deletedAt;
}
