package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Set;

public class ApartmentFeeStatusDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("apartment_id")
    public Long apartmentId;

    @JsonProperty("unpaid_fee_ids")
    public Set<Long> unpaidFeeIds;

    @JsonProperty("paid_fee_ids")
    public Set<Long> paidFeeIds;

    @JsonProperty("total_fee")
    public BigDecimal totalFee;

    @JsonProperty("total_paid")
    public BigDecimal totalPaid;

    @JsonProperty("balance")
    public BigDecimal balance;
}
