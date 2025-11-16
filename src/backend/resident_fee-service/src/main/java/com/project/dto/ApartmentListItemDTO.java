package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApartmentListItemDTO {
    @JsonProperty("apartment_id")
    public Long apartmentId;

    @JsonProperty("building")
    public String building;

    @JsonProperty("room_number")
    public String roomNumber;

    @JsonProperty("head_resident")
    public HeadResident headResident;
    public static class HeadResident {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("full_name")
        public String fullName;

        @JsonProperty("phone")
        public String phone;
    }
}
