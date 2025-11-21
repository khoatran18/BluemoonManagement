package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ResidentListResponseDTO {
    @JsonProperty("page")
    public Integer page;

    @JsonProperty("limit")
    public Integer limit;

    @JsonProperty("total_items")
    public Integer totalItems;

    @JsonProperty("residents")
    public List<ResidentListItemDTO> items;
}
