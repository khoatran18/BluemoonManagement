package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.Adjustment;

import java.math.BigDecimal;
import java.util.List;

public class AdjustmentDTO {

    ///////////////////////////// For Adjustment method /////////////////////////////

    /**
     * Response wrapper for getting a list of Adjustment records.
     */
    public static class GetAdjustmentsResponseDTO {
        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("adjustments")
        public List<GetAdjustmentsResponseItemDTO> Adjustments;
    }

    /**
     * Single Adjustment item inside GetAdjustmentsResponseDTO.
     */
    public static class GetAdjustmentsResponseItemDTO {
        @JsonProperty("adjustment_id")
        public Long AdjustmentId;

        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("adjustment_amount")
        public BigDecimal AdjustmentAmount;

        @JsonProperty("adjustment_type")
        public Adjustment.AdjustmentType AdjustmentType;

        @JsonProperty("reason")
        public String Reason;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;
    }

    /**
     * Response wrapper for getting a Adjustment records.
     */
    public static class GetAdjustmentResponseDTO {
        @JsonProperty("adjustment_id")
        public Long AdjustmentId;

        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("adjustment_amount")
        public BigDecimal AdjustmentAmount;

        @JsonProperty("adjustment_type")
        public Adjustment.AdjustmentType AdjustmentType;

        @JsonProperty("reason")
        public String Reason;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Request payload for creating a new Adjustment.
     */
    public static class PostAdjustmentRequestDTO {
        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("adjustment_amount")
        public BigDecimal AdjustmentAmount;

        @JsonProperty("adjustment_type")
        public Adjustment.AdjustmentType AdjustmentType;

        @JsonProperty("reason")
        public String Reason;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Request payload for updating an existing Adjustment.
     */
    public static class PutAdjustmentRequestDTO {
        @JsonProperty("adjustment_id")
        public Long AdjustmentId;

        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("adjustment_amount")
        public BigDecimal AdjustmentAmount;

        @JsonProperty("adjustment_type")
        public Adjustment.AdjustmentType AdjustmentType;

        @JsonProperty("reason")
        public String Reason;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;
    }
}
