package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResidentListItemDTO {
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
