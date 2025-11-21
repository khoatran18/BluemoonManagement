package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResidentUpdateDTO {

    @JsonProperty("resident_id")
    public Long residentId;

    @JsonProperty("full_name")
    public String fullName;

    @JsonProperty("email")
    public String email;

    @JsonProperty("phone_number")
    public String phoneNumber;
}
