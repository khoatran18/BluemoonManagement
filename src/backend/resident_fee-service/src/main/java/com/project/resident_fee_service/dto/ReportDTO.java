package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class ReportDTO {

    public static class ApartmentCommonReportDTO {
        @JsonProperty("resident_total")
        public Long ResidentTotal;

        @JsonProperty("room_total")
        public Long RoomTotal;

        @JsonProperty("building_total")
        public Long BuildingTotal;
    }

    public static class FeeCommonReportDTO {
        @JsonProperty("total_paid_fee_amount")
        public BigDecimal TotalPaidFeeAmount;

        @JsonProperty("active_fee_count")
        public Long ActiveFeeCount;

        @JsonProperty("draft_fee_count")
        public Long DraftFeeCount;

        @JsonProperty("closed_fee_count")
        public Long ClosedFeeCount;

        @JsonProperty("archived_fee_count")
        public Long ArchivedFeeCount;
    }

//    public static class FeeMonthReportDTO {
//        @JsonProperty("month_fee_report")
//        public List<FeeMonthReportItemDTO> MonthFeeReports;
//    }
//    public static class FeeMonthReportItemDTO {
//        @JsonProperty("month")
//        public String Month;
//
//        @JsonProperty("total_fee_amount")
//        public BigDecimal TotalFeeAmount;
//    }

}
