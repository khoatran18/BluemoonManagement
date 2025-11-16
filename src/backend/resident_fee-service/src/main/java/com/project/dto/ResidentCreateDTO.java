package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResidentCreateDTO {

    @JsonProperty("full_name")
    public String fullName;

    @JsonProperty("email")
    public String email;

    @JsonProperty("phone_number")
    public String phoneNumber;
}
