package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApartmentDetailsDTO {
    @JsonProperty("apartment_id")
    public Long apartmentId;

    @JsonProperty("building")
    public String building;

    @JsonProperty("room_number")
    public String roomNumber;

    @JsonProperty("head_resident_id")
    public Long headResidentId;

    @JsonProperty("residents")
    public List<ResidentInfo> residents;
    public static class ResidentInfo {
        @JsonProperty("resident_id")
        public Long residentId;

        @JsonProperty("full_name")
        public String fullName;
    }
}
