package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResidentDTO {

    public static class ResidentListResponseDTO {
        @JsonProperty("page")
        public Integer page;

        @JsonProperty("limit")
        public Integer limit;

        @JsonProperty("total_items")
        public Long totalItems;

        @JsonProperty("residents")
        public List<ResidentListItemDTO> items;
    }

    public static class ResidentListItemDTO {
        @JsonProperty("resident_id")
        public Long residentId;

        @JsonProperty("full_name")
        public String fullName;

        @JsonProperty("email")
        public String email;

        @JsonProperty("phone_number")
        public String phoneNumber;

        @JsonProperty("apartment")
        public ApartmentInfo apartment;
        public static class ApartmentInfo {
            @JsonProperty("id")
            public Long id;

            @JsonProperty("building")
            public String building;

            @JsonProperty("room_number")
            public String roomNumber;
        }

        @JsonProperty("is_head")
        public Boolean isHead;
    }

    public static class ResidentDetailsDTO {
        @JsonProperty("resident_id")
        public Long residentId;

        @JsonProperty("full_name")
        public String fullName;

        @JsonProperty("email")
        public String email;

        @JsonProperty("phone_number")
        public String phoneNumber;

        @JsonProperty("apartment")
        public ApartmentInfo apartment;
        public static class ApartmentInfo {
            @JsonProperty("id")
            public Long id;

            @JsonProperty("building")
            public String building;

            @JsonProperty("room_number")
            public String roomNumber;
        }

        @JsonProperty("is_head")
        public Boolean isHead;
    }

    public static class ResidentUpdateDTO {
        @JsonProperty("full_name")
        public String fullName;

        @JsonProperty("email")
        public String email;

        @JsonProperty("phone_number")
        public String phoneNumber;
    }

    public static class ResidentCreateDTO {
        @JsonProperty("full_name")
        public String fullName;

        @JsonProperty("email")
        public String email;

        @JsonProperty("phone_number")
        public String phoneNumber;
    }
}
