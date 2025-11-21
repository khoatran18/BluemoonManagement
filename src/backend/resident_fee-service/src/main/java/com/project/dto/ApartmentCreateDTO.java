package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApartmentCreateDTO {
    @JsonProperty("building")
    public String building;

    @JsonProperty("room_number")
    public String roomNumber;
}
