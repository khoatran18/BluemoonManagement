package com.project.mapper;

import com.project.dto.FeeTypeDTO;
import com.project.entity.FeeType;

import java.util.ArrayList;
import java.util.List;

public class FeeTypeMapper {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of FeeType entity to list of GetFeeTypesResponseDTO.
     * Used when returning a list of FeeType records to client.
     */
    public List<FeeTypeDTO.GetFeeTypesResponseItemDTO> GetFeeTypesResponseItemsEntityToDTO(List<FeeType> entityList) {

        // Init result
        List<FeeTypeDTO.GetFeeTypesResponseItemDTO>  dtoList = new ArrayList<>();

        // Create result
        for (FeeType entity : entityList) {
            FeeTypeDTO.GetFeeTypesResponseItemDTO dto = GetFeeTypesResponseItemEntityToDTO(entity);
            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }

    /**
     * Convert a FeeType record to a GetFeeTypesResponseItemDTO.
     * Used when returning a list of FeeType records to client, called in GetFeeTypesResponseItemsEntityToDTO.
     */
    public FeeTypeDTO.GetFeeTypesResponseItemDTO GetFeeTypesResponseItemEntityToDTO(FeeType entity) {

        // Init result
        FeeTypeDTO.GetFeeTypesResponseItemDTO dto = new FeeTypeDTO.GetFeeTypesResponseItemDTO();

        // Create result
        dto.Id = entity.getFeeTypeId();
        dto.Name = entity.getFeeTypeName();

        // Return result
        return dto;
    }

    /**
     * Convert FeeType entity to GetFeeTypeResponseDTO.
     * Used when returning a FeeType record to client.
     */
    public FeeTypeDTO.GetFeeTypeResponseDTO GetFeeTypeResponseEntityToDTO(FeeType entity) {

        // Init result
        FeeTypeDTO.GetFeeTypeResponseDTO dto = new FeeTypeDTO.GetFeeTypeResponseDTO();

        // Create result
        dto.Id = entity.getFeeTypeId();
        dto.Name = entity.getFeeTypeName();

        // Return result
        return dto;
    }

}
