package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
public class ApartmentDTO {

    public static class ApartmentListResponseDTO {
        @JsonProperty("page")
        public Integer page;

        @JsonProperty("limit")
        public Integer limit;

        @JsonProperty("total_items")
        public Long totalItems;

        @JsonProperty("items")
        public List<ApartmentListItemDTO> items;
    }

    public static class ApartmentListItemDTO {
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

    public static class ApartmentDetailsDTO {
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

    public static class ApartmentUpdateDTO {
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
            @JsonProperty("id")
            public Long id;
        }
    }

    public static class ApartmentCreateDTO {
        @JsonProperty("building")
        public String building;

        @JsonProperty("room_number")
        public String roomNumber;
    }
}
