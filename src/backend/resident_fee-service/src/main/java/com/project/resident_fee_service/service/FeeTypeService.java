package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.FeeTypeDTO;
import com.project.resident_fee_service.entity.FeeType;
import com.project.resident_fee_service.exception.InternalServerException;
import com.project.resident_fee_service.exception.NotFoundException;
import com.project.resident_fee_service.mapper.FeeTypeMapper;
import com.project.resident_fee_service.repository.FeeTypeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FeeTypeService {

    @Inject
    FeeTypeRepository feeTypeRepository;

    FeeTypeMapper feeTypeMapper = new FeeTypeMapper();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Handle logic for API getAllFeeTypes: retrieve database and convert to response DTO
     */
    public FeeTypeDTO.GetFeeTypesResponseDTO getFeeTypesByFilter() {

        // Prepare data for response
        FeeTypeDTO.GetFeeTypesResponseDTO responseData = new FeeTypeDTO.GetFeeTypesResponseDTO();

        try {
            // Retrieve database
            List<FeeType> entityList = feeTypeRepository.getAll();

            // Prepare data for response
            List<FeeTypeDTO.GetFeeTypesResponseItemDTO> dtoList = feeTypeMapper.GetFeeTypesResponseItemsEntityToDTO(entityList);

            // Create data for response
            responseData.FeeTypes = dtoList;

            // Return data for response
            return responseData;
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * Handle logic for API getFeeTypeById: retrieve database and convert to response DTO
     */
    public FeeTypeDTO.GetFeeTypeResponseDTO getFeeTypeById(Long feeTypeId) {

        try {
            // Retrieve database
            FeeType feeType = feeTypeRepository.findById(feeTypeId);
            if (feeType == null)
                throw new NotFoundException("Fee Type not found with id: " + feeTypeId);

            // Return data for response
            return feeTypeMapper.GetFeeTypeResponseEntityToDTO(feeType);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
