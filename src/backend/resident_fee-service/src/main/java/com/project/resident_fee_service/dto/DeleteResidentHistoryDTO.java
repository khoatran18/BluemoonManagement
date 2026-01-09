package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class DeleteResidentHistoryDTO {

    ///////////////////////////// For Get method /////////////////////////////

    public static class GetDeleteResidentHistoriesResponseDTO {

        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("delete_resident_histories")
        public List<GetDeleteResidentHistoriesResponseItemDTO> DeleteResidentHistories;
    }

    public static class GetDeleteResidentHistoriesResponseItemDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("resident_id")
        public Long ResidentID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("owner_resident_id")
        public Long OwnerResidentID;

        @JsonProperty("full_name")
        public String FullName;

        @JsonProperty("phone_number")
        public String PhoneNumber;

        @JsonProperty("email")
        public String Email;

        @JsonProperty("is_head")
        public Boolean IsHead;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;
    }

    public static class GetDeleteResidentHistoryResponseDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("resident_id")
        public Long ResidentID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("owner_resident_id")
        public Long OwnerResidentID;

        @JsonProperty("full_name")
        public String FullName;

        @JsonProperty("phone_number")
        public String PhoneNumber;

        @JsonProperty("email")
        public String Email;

        @JsonProperty("is_head")
        public Boolean IsHead;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;
    }
}
