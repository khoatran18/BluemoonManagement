package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.entity.FeeType;

import java.math.BigDecimal;
import java.util.List;

public class PayHistoryDTO {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Response wrapper for getting a list of PayHistory records.
     */
    public static class GetPayHistoriesResponseDTO {
        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("pay_histories")
        public List<GetPayHistoriesResponseItemDTO> PayHistories;
    }

    /**
     * Single PayHistory item inside GetPayHistoriesResponseDTO.
     */
    public static class GetPayHistoriesResponseItemDTO {
        @JsonProperty("pay_history_id")
        public Long PayHistoryID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_type_name")
        public FeeType.FeeTypeName FeeTypeName;

        @JsonProperty("fee_category_name")
        public String FeeCategoryName;

        @JsonProperty("pay_datetime")
        public String PayDatetime;

        @JsonProperty("pay_amount")
        public BigDecimal PayAmount;

        @JsonProperty("pay_note")
        public String PayNote;
    }

    /**
     * Response wrapper for getting a PayHistory record.
     */
    public static class GetPayHistoryResponseDTO {
        @JsonProperty("pay_history_id")
        public Long PayHistoryID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_type_name")
        public FeeType.FeeTypeName FeeTypeName;

        @JsonProperty("fee_category_name")
        public String FeeCategoryName;

        @JsonProperty("pay_datetime")
        public String PayDatetime;

        @JsonProperty("pay_amount")
        public BigDecimal PayAmount;

        @JsonProperty("pay_note")
        public String PayNote;
    }


    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Request payload for creating a new PayHistory.
     */
    public static class PostFeeRequestDTO {
        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("pay_datetime")
        public String PayDatetime;

        @JsonProperty("pay_amount")
        public BigDecimal PayAmount;

        @JsonProperty("pay_note")
        public String PayNote;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Request payload for updating an existing PayHistory.
     */
    public static class PutFeeRequestDTO {
        @JsonProperty("pay_history_id")
        public Long PayHistoryID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("pay_datetime")
        public String PayDatetime;

        @JsonProperty("pay_amount")
        public BigDecimal PayAmount;

        @JsonProperty("pay_note")
        public String PayNote;
    }

}
