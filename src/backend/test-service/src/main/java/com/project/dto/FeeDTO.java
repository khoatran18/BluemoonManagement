package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;


public class FeeDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("fee_name")
    @NotBlank
    public String feeName;

    @JsonProperty("fee_amount")
    @NotNull
    public BigDecimal feeAmount;

    @JsonProperty("applicable_month")
    public String applicableMonth;

    @JsonProperty("effective_date")
    public LocalDate effectiveDate;

    @JsonProperty("expiry_date")
    public LocalDate expiryDate;

    @JsonProperty("status")
    public String status;
}
