package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.entity.Fee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class FeeDTO {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Response wrapper for getting a list of Fee records.
     */
    public static class GetFeesResponseDTO {
        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("fees")
        public List<GetFeesResponseItemDTO> Fees;
    }

    /**
     * Single Fee item inside GetFeesResponseDTO.
     */
    public static class GetFeesResponseItemDTO {
        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("fee_amount")
        public BigDecimal FeeAmount;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;
    }

    /**
     * Response wrapper for getting a Fee records.
     */
    public static class GetFeeResponseDTO {
        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("fee_amount")
        public BigDecimal FeeAmount;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Request payload for creating a new Fee.
     */
    public static class PostFeeRequestDTO {
        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("fee_amount")
        public BigDecimal FeeAmount;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Request payload for updating an existing Fee.
     */
    public static class PutFeeRequestDTO {
        @JsonProperty("fee_id")
        public Long FeeId;

        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("fee_amount")
        public BigDecimal FeeAmount;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("effective_date")
        public String EffectiveDate;

        @JsonProperty("expiry_date")
        public String ExpiryDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;
    }

}
