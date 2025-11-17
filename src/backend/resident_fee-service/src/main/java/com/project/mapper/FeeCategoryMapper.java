package com.project.mapper;

import com.project.dto.FeeCategoryDTO;
import com.project.entity.FeeCategory;
import com.project.entity.FeeType;
import com.project.repository.FeeTypeRepository;

import java.util.ArrayList;
import java.util.List;

public class FeeCategoryMapper {

    private final FeeTypeRepository feeTypeRepository =  new FeeTypeRepository();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of FeeCategory entity to list of GetFeeCategoriesResponseItemDTO.
     * Used when returning a list of FeeCategory records to client.
     */
    public List<FeeCategoryDTO.GetFeeCategoriesResponseItemDTO> GetFeeCategoriesResponseItemsEntityToDTO(List<FeeCategory> entityList) {

        // Init result
        List<FeeCategoryDTO.GetFeeCategoriesResponseItemDTO> dtoList = new ArrayList<FeeCategoryDTO.GetFeeCategoriesResponseItemDTO>();

        // Create result
        for (FeeCategory entity : entityList) {
            FeeCategoryDTO.GetFeeCategoriesResponseItemDTO dto = new FeeCategoryDTO.GetFeeCategoriesResponseItemDTO();

            dto.FeeCategoryId = entity.getFeeCategoryId();
            dto.Name = entity.getFeeCategoryName();
            dto.Description = entity.getFeeCategoryDescription();
            dto.FeeTypeName = entity.getFeeType().getFeeTypeName();

            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }

    /**
     * Convert a FeeCategory record to a GetFeeCategoriesResponseItemDTO.
     * Used when returning a list of FeeCategory records to client, called in GetFeeCategoriesResponseItemsEntityToDTO.
     */
    public FeeCategoryDTO.GetFeeCategoriesResponseItemDTO GetFeeCategoriesResponseItemEntityToDTO(FeeCategory entity) {

        // Init result
        FeeCategoryDTO.GetFeeCategoriesResponseItemDTO dto = new FeeCategoryDTO.GetFeeCategoriesResponseItemDTO();

        // Create result
        dto.FeeCategoryId = entity.getFeeCategoryId();
        dto.Name = entity.getFeeCategoryName();
        dto.Description = entity.getFeeCategoryDescription();
        dto.FeeTypeName = entity.getFeeType().getFeeTypeName();

        // Return result
        return dto;
    }

    /**
     * Convert FeeCategory entity to GetFeeCategoryResponseDTO.
     * Used when returning a FeeCategory record to client.
     */
    public FeeCategoryDTO.GetFeeCategoryResponseDTO GetFeeCategoryResponseEntityToDTO(FeeCategory entity) {

        // Init result
        FeeCategoryDTO.GetFeeCategoryResponseDTO  dto = new FeeCategoryDTO.GetFeeCategoryResponseDTO();

        // Create result
        dto.FeeCategoryId = entity.getFeeCategoryId();
        dto.Name = entity.getFeeCategoryName();
        dto.Description = entity.getFeeCategoryDescription();
        dto.FeeTypeName = entity.getFeeType().getFeeTypeName();

        // Return result
        return dto;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Convert PostFeeCategoryRequestDTO to FeeCategory entity.
     * Used when creating a new FeeCategory record.
     */
    public FeeCategory PostFeeCategoryRequestDTOToEntity(FeeCategoryDTO.PostFeeCategoryRequestDTO dto) {

        // Init result
        FeeCategory entity = new FeeCategory();

        // Prepare result
        FeeType feeType = feeTypeRepository.findById(dto.FeeTypeId);

        // Create result
        entity.setFeeCategoryName(dto.Name);
        entity.setFeeCategoryDescription(dto.Description);
        entity.setFeeType(feeType);

        // Return result
        return entity;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Convert PutFeeCategoryRequestDTO to FeeCategory entity.
     * Used when updating an existed FeeCategory record.
     */
    public FeeCategory PutFeeCategoryRequestDTOToEntity(FeeCategoryDTO.PutFeeCategoryRequestDTO dto) {

        // Init result
        FeeCategory entity = new FeeCategory();

        // Prepare result
        FeeType feeType = feeTypeRepository.findById(dto.FeeTypeId);

        // Create result
        entity.setFeeCategoryId(dto.FeeCategoryId);
        entity.setFeeCategoryName(dto.Name);
        entity.setFeeCategoryDescription(dto.Description);
        entity.setFeeType(feeType);

        // Return result
        return entity;
    }


}
