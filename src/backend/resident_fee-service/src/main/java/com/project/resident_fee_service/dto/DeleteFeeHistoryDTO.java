package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.Fee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DeleteFeeHistoryDTO {

    ///////////////////////////// For Get method /////////////////////////////

    public static class GetDeleteFeeHistoriesResponseDTO {

        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("delete_fee_histories")
        public List<GetDeleteFeeHistoriesResponseItemDTO> DeleteFeeHistories;
    }

    public static class GetDeleteFeeHistoriesResponseItemDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("fee_type_id")
        public Long FeeTypeID;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("amount")
        public BigDecimal Amount;

        @JsonProperty("start_date")
        public LocalDate StartDate;

        @JsonProperty("end_date")
        public LocalDate EndDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;
    }

    public static class GetDeleteFeeHistoryResponseDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("fee_id")
        public Long FeeID;

        @JsonProperty("fee_type_id")
        public Long FeeTypeID;

        @JsonProperty("fee_category_id")
        public Long FeeCategoryID;

        @JsonProperty("fee_name")
        public String FeeName;

        @JsonProperty("fee_description")
        public String FeeDescription;

        @JsonProperty("applicable_month")
        public String ApplicableMonth;

        @JsonProperty("amount")
        public BigDecimal Amount;

        @JsonProperty("start_date")
        public LocalDate StartDate;

        @JsonProperty("end_date")
        public LocalDate EndDate;

        @JsonProperty("status")
        public Fee.FeeStatus Status;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;
    }
}
