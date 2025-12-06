package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.entity.FeeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class ApartmentFeeStatusDTO {

    public static class FeeStatusDTO {

        @JsonProperty("apartment_id")
        public Long apartmentId;

        @JsonProperty("unpaid_fees")
        public Set<FeeDetails> unpaidFees;

        public static class FeeDetails {
            @JsonProperty("fee_id")
            public Long feeId;

            @JsonProperty("fee_name")
            public String feeName;

            @JsonProperty("fee_amount")
            public BigDecimal feeAmount;

            @JsonProperty("fee_type_id")
            public Long feeTypeId;

            @JsonProperty("fee_type_name")
            public FeeType.FeeTypeName feeTypeName;

            @JsonProperty("fee_category_id")
            public Long feeCategoryId;

            @JsonProperty("fee_category_name")
            public String feeCategoryName;

            @JsonProperty("effective_date")
            public LocalDate effectiveDate;

            @JsonProperty("expiry_date")
            public LocalDate expiryDate;

            @JsonProperty("fee_description")
            public String feeDescription;
        }

        @JsonProperty("adjustments")
        public Set<AdjustmentDetails> adjustments;

        public static class AdjustmentDetails {
            @JsonProperty("adjustment_id")
            public Long adjustmentId;

            @JsonProperty("fee_id")
            public Long feeId;

            @JsonProperty("adjustment_amount")
            public BigDecimal adjustmentAmount;

            @JsonProperty("adjustment_type")
            public Adjustment.AdjustmentType adjustmentType;

            @JsonProperty("reason")
            public String reason;

            @JsonProperty("effective_date")
            public LocalDate effectiveDate;

            @JsonProperty("expiry_date")
            public LocalDate expiryDate;
        }
        @JsonProperty("extra_adjustments")
        public Set<ExtraAdjustmentDetails> extraAdjustments;

        public static class ExtraAdjustmentDetails {
            @JsonProperty("adjustment_id")
            public Long adjustmentId;

            @JsonProperty("fee_id")
            public Long feeId;

            @JsonProperty("adjustment_amount")
            public BigDecimal adjustmentAmount;

            @JsonProperty("adjustment_type")
            public Adjustment.AdjustmentType adjustmentType;

            @JsonProperty("reason")
            public String reason;

            @JsonProperty("effective_date")
            public LocalDate effectiveDate;

            @JsonProperty("expiry_date")
            public LocalDate expiryDate;
        }

        @JsonProperty("total_paid")
        public BigDecimal totalPaid;

        @JsonProperty("balance")
        public BigDecimal balance;

        @JsonProperty("updated_at")
        public LocalDateTime updatedAt;
    }


    public static class FeeStatusUpdateDTO {
        @JsonProperty("total_paid")
        public BigDecimal totalPaid;

        @JsonProperty("balance")
        public BigDecimal balance;

        @JsonProperty("paid_fees")
        public Set<FeeRef> paidFees;

        @JsonProperty("unpaid_fees")
        public Set<FeeRef> unpaidFees;

        public static class FeeRef {
            @JsonProperty("fee_id")
            public Long feeId;
        }
    }
}
