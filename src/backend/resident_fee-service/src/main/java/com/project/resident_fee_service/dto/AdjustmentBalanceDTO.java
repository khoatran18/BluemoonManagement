package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.FeeType;

import java.math.BigDecimal;
import java.util.List;

public class AdjustmentBalanceDTO {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Response wrapper for getting a list of PayHistory records.
     */
    public static class GetAdjustmentBalancesResponseDTO {
        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("adjustment_balances")
        public List<AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO> AdjustmentBalances;
    }

    /**
     * Single AdjustmentBalance item inside GetAdjustmentBalancesResponseDTO.
     */
    public static class GetAdjustmentBalancesResponseItemDTO {
        @JsonProperty("adjustment_balance_id")
        public Long AdjustmentBalanceID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("adjustment_id")
        public Long AdjustmentID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("adjustment_reason")
        public String AdjustmentReason;

        @JsonProperty("old_balance")
        public BigDecimal OldBalance;

        @JsonProperty("new_balance")
        public BigDecimal NewBalance;

        @JsonProperty("fee_start_datetime")
        public String FeeStartDatetime;

        @JsonProperty("adjustment_start_datetime")
        public String AdjustmentStartDatetime;

        @JsonProperty("adjustment_balance_note")
        public String AdjustmentBalanceNote;
    }

    /**
     * Response wrapper for getting a AdjustmentBalance record.
     */
    public static class GetAdjustmentBalanceResponseDTO {
        @JsonProperty("adjustment_balance_id")
        public Long AdjustmentBalanceID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("adjustment_id")
        public Long AdjustmentID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("adjustment_reason")
        public String AdjustmentReason;

        @JsonProperty("old_balance")
        public BigDecimal OldBalance;

        @JsonProperty("new_balance")
        public BigDecimal NewBalance;

        @JsonProperty("fee_start_datetime")
        public String FeeStartDatetime;

        @JsonProperty("adjustment_start_datetime")
        public String AdjustmentStartDatetime;

        @JsonProperty("adjustment_balance_note")
        public String AdjustmentBalanceNote;
    }


    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Request payload for creating a new AdjustmentBalance.
     */
    public static class PostFeeRequestDTO {

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("adjustment_id")
        public Long AdjustmentID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("adjustment_reason")
        public String AdjustmentReason;

        @JsonProperty("old_balance")
        public BigDecimal OldBalance;

        @JsonProperty("new_balance")
        public BigDecimal NewBalance;

        @JsonProperty("adjustment_balance_note")
        public String AdjustmentBalanceNote;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Request payload for updating an existing AdjustmentBalance.
     */
    public static class PutFeeRequestDTO {
        @JsonProperty("adjustment_balance_id")
        public Long AdjustmentBalanceID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("adjustment_id")
        public Long AdjustmentID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("adjustment_reason")
        public String AdjustmentReason;

        @JsonProperty("old_balance")
        public BigDecimal OldBalance;

        @JsonProperty("new_balance")
        public BigDecimal NewBalance;

        @JsonProperty("adjustment_balance_note")
        public String AdjustmentBalanceNote;
    }

}
