package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApartmentDTO {

    @JsonProperty("id")
    public Long id;

    @NotBlank
    @Size(max = 30)
    public String building;

    @JsonProperty("room_number")
    @NotBlank
    @Size(max = 6)
    public String roomNumber;
}