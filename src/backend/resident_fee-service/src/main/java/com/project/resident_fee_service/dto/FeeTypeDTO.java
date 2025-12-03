package com.project.resident_fee_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.resident_fee_service.entity.FeeType;

import java.util.List;

public class FeeTypeDTO {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Response wrapper for getting a list of FeeType records.
     */
    public static class GetFeeTypesResponseDTO {
        @JsonProperty("fee_types")
        public List<GetFeeTypesResponseItemDTO> FeeTypes;
    }

    /**
     * Single FeeType item inside GetFeeTypesResponseDTO.
     */
    public static class GetFeeTypesResponseItemDTO {
        @JsonProperty("id")
        public Long Id;

        @JsonProperty("name")
        public FeeType.FeeTypeName Name;
    }

    /**
     * Response wrapper for getting a FeeType records.
     */
    public static class GetFeeTypeResponseDTO {
        @JsonProperty("id")
        public Long Id;

        @JsonProperty("name")
        public FeeType.FeeTypeName Name;
    }

}
