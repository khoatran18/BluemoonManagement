package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class DeleteApartmentHistoryDTO {

    ///////////////////////////// For Get method /////////////////////////////

    public static class GetDeleteApartmentHistoriesResponseDTO {

        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("delete_apartment_histories")
        public List<GetDeleteApartmentHistoriesResponseItemDTO> DeleteApartmentHistories;
    }

    public static class GetDeleteApartmentHistoriesResponseItemDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("building")
        public String Building;

        @JsonProperty("room_number")
        public String RoomNumber;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;
    }

    public static class GetDeleteApartmentHistoryResponseDTO {

        @JsonProperty("history_id")
        public Long HistoryID;

        @JsonProperty("apartment_id")
        public Long ApartmentID;

        @JsonProperty("building")
        public String Building;

        @JsonProperty("room_number")
        public String RoomNumber;

        @JsonProperty("deleted_at")
        public LocalDateTime DeletedAt;

    }
}
