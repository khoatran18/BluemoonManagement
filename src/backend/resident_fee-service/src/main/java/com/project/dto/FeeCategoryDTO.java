package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.entity.FeeType;

import java.util.List;

public class FeeCategoryDTO {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Response wrapper for getting a list of FeeCategory records.
     */
    public static class GetFeeCategoriesResponseDTO {
        @JsonProperty("page")
        public Integer Page;

        @JsonProperty("limit")
        public Integer Limit;

        @JsonProperty("total_items")
        public Long TotalItems;

        @JsonProperty("fee_categories")
        public List<GetFeeCategoriesResponseItemDTO> FeeCategories;
    }

    /**
     * Single FeeCategory item inside GetFeeCategoriesResponseDTO.
     */
    public static class GetFeeCategoriesResponseItemDTO {
        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("name")
        public String Name;

        @JsonProperty("description")
        public String Description;

        @JsonProperty("fee_type_name")
        public FeeType.FeeTypeName FeeTypeName;
    }

    /**
     * Response wrapper for getting a FeeCategory records.
     */
    public static class GetFeeCategoryResponseDTO {
        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("name")
        public String Name;

        @JsonProperty("description")
        public String Description;

        @JsonProperty("fee_type_name")
        public FeeType.FeeTypeName FeeTypeName;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Request payload for creating a new FeeCategory.
     */
    public static class PostFeeCategoryRequestDTO {
        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("name")
        public String Name;

        @JsonProperty("description")
        public String Description;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Request payload for updating an existing FeeCategory.
     */
    public static class PutFeeCategoryRequestDTO {
        @JsonProperty("fee_category_id")
        public Long FeeCategoryId;

        @JsonProperty("fee_type_id")
        public Long FeeTypeId;

        @JsonProperty("name")
        public String Name;

        @JsonProperty("description")
        public String Description;
    }

}
