package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.FeeDTO;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.entity.FeeCategory;
import com.project.resident_fee_service.entity.FeeType;
import com.project.resident_fee_service.repository.FeeCategoryRepository;
import com.project.resident_fee_service.repository.FeeTypeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeeMapper {

    private final FeeTypeRepository feeTypeRepository =  new FeeTypeRepository();
    private final FeeCategoryRepository feeCategoryRepository =  new FeeCategoryRepository();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of Fee entity to list of GetFeesResponseItemDTO.
     * Used when returning a list of Fee records to client.
     */
    public List<FeeDTO.GetFeesResponseItemDTO> GetFeesResponseItemsEntityToDTO(List<Fee> entityList) {

        // Init result
        List<FeeDTO.GetFeesResponseItemDTO> dtoList = new ArrayList<FeeDTO.GetFeesResponseItemDTO>();

        // Create result
        for (Fee entity : entityList) {
            FeeDTO.GetFeesResponseItemDTO dto = GetFeesResponseItemEntityToDTO(entity);
            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }

    /**
     * Convert a Fee record to a GetFeesResponseItemDTO.
     * Used when returning a list of Fee records to client, called in GetFeesResponseItemsEntityToDTO.
     */
    public FeeDTO.GetFeesResponseItemDTO GetFeesResponseItemEntityToDTO(Fee entity) {

        // Init result
        FeeDTO.GetFeesResponseItemDTO dto = new FeeDTO.GetFeesResponseItemDTO();

        // Prepare result
        String effectiveDate = LocalDateMapper.LocalDateToString(entity.getStartDate());
        String expiryDate = LocalDateMapper.LocalDateToString(entity.getEndDate());

        // Create result
        dto.FeeId = entity.getFeeId();
        dto.FeeTypeId = entity.getFeeType().getFeeTypeId();
        dto.FeeCategoryId = entity.getFeeCategory().getFeeCategoryId();
        dto.FeeName = entity.getFeeName();
        dto.FeeDescription = entity.getFeeDescription();
        dto.FeeAmount = entity.getAmount();
        dto.ApplicableMonth = entity.getApplicableMonth();
        dto.Status = entity.getStatus();
        dto.EffectiveDate = effectiveDate;
        dto.ExpiryDate = expiryDate;

        // Return result
        return dto;
    }

    /**
     * Convert Fee entity to GetFeeResponseDTO.
     * Used when returning a Fee record to client.
     */
    public FeeDTO.GetFeeResponseDTO GetFeeResponseEntityToDTO(Fee entity) {

        // Init result
        FeeDTO.GetFeeResponseDTO dto = new FeeDTO.GetFeeResponseDTO();

        // Prepare result
        String effectiveDate = LocalDateMapper.LocalDateToString(entity.getStartDate());
        String expiryDate = LocalDateMapper.LocalDateToString(entity.getEndDate());

        // Create result
        dto.FeeId = entity.getFeeId();
        dto.FeeTypeId = entity.getFeeType().getFeeTypeId();
        dto.FeeCategoryId = entity.getFeeCategory().getFeeCategoryId();
        dto.FeeName = entity.getFeeName();
        dto.FeeDescription = entity.getFeeDescription();
        dto.FeeAmount = entity.getAmount();
        dto.ApplicableMonth = entity.getApplicableMonth();
        dto.Status = entity.getStatus();
        dto.EffectiveDate = effectiveDate;
        dto.ExpiryDate = expiryDate;

        // Return result
        return dto;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Convert PostFeeRequestDTO to Fee entity.
     * Used when creating a new Fee record.
     */
    public Fee PostFeeRequestDTOToEntity(FeeDTO.PostFeeRequestDTO dto){

        // Init result
        Fee entity = new Fee();

        // Prepare for result
        FeeType feeType = feeTypeRepository.findById(dto.FeeTypeId);
        FeeCategory feeCategory = feeCategoryRepository.findById(dto.FeeCategoryId);
        LocalDate startDate = LocalDateMapper.StringToLocalDate(dto.EffectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(dto.ExpiryDate);

        // Create result
        entity.setFeeType(feeType);
        entity.setFeeCategory(feeCategory);
        entity.setFeeName(dto.FeeName);
        entity.setFeeDescription(dto.FeeDescription);
        entity.setAmount(dto.FeeAmount);
        entity.setApplicableMonth(dto.ApplicableMonth);
        entity.setStatus(dto.Status);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        // Return result
        return entity;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Convert PutFeeRequestDTO to Fee entity.
     * Used when updating an existed Fee record.
     */
    public Fee PutFeeRequestDTOToEntity(FeeDTO.PutFeeRequestDTO dto) {

        // Init result
        Fee entity = new Fee();

        // Prepare result
        FeeType feeType = feeTypeRepository.findById(dto.FeeTypeId);
        FeeCategory feeCategory = feeCategoryRepository.findById(dto.FeeCategoryId);
        LocalDate startDate = LocalDateMapper.StringToLocalDate(dto.EffectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(dto.ExpiryDate);

        // Create result
        entity.setFeeId(dto.FeeId);
        entity.setFeeType(feeType);
        entity.setFeeCategory(feeCategory);
        entity.setFeeName(dto.FeeName);
        entity.setFeeDescription(dto.FeeDescription);
        entity.setAmount(dto.FeeAmount);
        entity.setApplicableMonth(dto.ApplicableMonth);
        entity.setStatus(dto.Status);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        // Return result
        return entity;
    }

}
