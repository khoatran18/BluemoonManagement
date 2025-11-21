package com.project.mapper;

import com.project.dto.AdjustmentDTO;
import com.project.dto.AdjustmentDTO;
import com.project.entity.Adjustment;
import com.project.entity.Adjustment;
import com.project.entity.Fee;
import com.project.repository.AdjustmentRepository;
import com.project.repository.FeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdjustmentMapper {

    private final AdjustmentRepository adjustmentRepository =  new AdjustmentRepository();
    private final FeeRepository feeRepository =  new FeeRepository();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of Adjustment entity to list of GetAdjustmentsResponseItemDTO.
     * Used when returning a list of Adjustment records to client.
     */
    public List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> GetAdjustmentsResponseItemsEntityToDTO(List<Adjustment> entityList) {

        // Init result
        List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> dtoList = new ArrayList<AdjustmentDTO.GetAdjustmentsResponseItemDTO>();

        // Create result
        for (Adjustment entity : entityList) {
            AdjustmentDTO.GetAdjustmentsResponseItemDTO dto = GetAdjustmentsResponseItemEntityToDTO(entity);
            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }

    /**
     * Convert a Adjustment record to a GetAdjustmentsResponseItemDTO.
     * Used when returning a list of Adjustment records to client, called in GetAdjustmentsResponseItemsEntityToDTO.
     */
    public AdjustmentDTO.GetAdjustmentsResponseItemDTO GetAdjustmentsResponseItemEntityToDTO(Adjustment entity) {

        // Init result
        AdjustmentDTO.GetAdjustmentsResponseItemDTO dto = new AdjustmentDTO.GetAdjustmentsResponseItemDTO();

        // Prepare result
        String effectiveDate = LocalDateMapper.LocalDateToString(entity.getStartDate());
        String expiryDate = LocalDateMapper.LocalDateToString(entity.getEndDate());

        // Create result
        dto.AdjustmentId = entity.getAdjustmentId();
        dto.FeeId = (entity.getFee() == null)
                ? -1
                : entity.getFee().getFeeId();
        dto.AdjustmentAmount = entity.getAdjustmentAmount();
        dto.AdjustmentType = entity.getAdjustmentType();
        dto.Reason = entity.getReason();
        dto.EffectiveDate = effectiveDate;
        dto.ExpiryDate = expiryDate;

        // Return result
        return dto;
    }

    /**
     * Convert Adjustment entity to GetAdjustmentResponseDTO.
     * Used when returning an Adjustment record to client.
     */
    public AdjustmentDTO.GetAdjustmentResponseDTO GetAdjustmentResponseEntityToDTO(Adjustment entity) {

        // Init result
        AdjustmentDTO.GetAdjustmentResponseDTO dto = new AdjustmentDTO.GetAdjustmentResponseDTO();

        // Prepare result
        String effectiveDate = LocalDateMapper.LocalDateToString(entity.getStartDate());
        String expiryDate = LocalDateMapper.LocalDateToString(entity.getEndDate());

        // Create result
        dto.AdjustmentId = entity.getAdjustmentId();
        dto.FeeId = (entity.getFee() == null)
                ? -1
                : entity.getFee().getFeeId();
        dto.AdjustmentAmount = entity.getAdjustmentAmount();
        dto.AdjustmentType = entity.getAdjustmentType();
        dto.Reason = entity.getReason();
        dto.EffectiveDate = effectiveDate;
        dto.ExpiryDate = expiryDate;

        // Return result
        return dto;
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Convert PostAdjustmentRequestDTO to Adjustment entity.
     * Used when creating a new Adjustment record.
     */
    public Adjustment PostAdjustmentRequestDTOToEntity(AdjustmentDTO.PostAdjustmentRequestDTO dto){

        // Init result
        Adjustment entity = new Adjustment();

        // Prepare for result
        Fee fee = null;
        if (dto.FeeId > 0) {
            fee = feeRepository.findById(dto.FeeId);
        }

        LocalDate startDate = LocalDateMapper.StringToLocalDate(dto.EffectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(dto.ExpiryDate);

        // Create result
        entity.setFee(fee);
        entity.setAdjustmentAmount(dto.AdjustmentAmount);
        entity.setAdjustmentType(dto.AdjustmentType);
        entity.setReason(dto.Reason);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        // Return result
        return entity;
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Convert PutAdjustmentRequestDTO to Adjustment entity.
     * Used when updating an existed Adjustment record.
     */
    public Adjustment PutAdjustmentRequestDTOToEntity(AdjustmentDTO.PutAdjustmentRequestDTO dto) {

        // Init result
        Adjustment entity = new Adjustment();

        // Prepare for result
        Fee fee = null;
        if (dto.FeeId > 0) {
            fee = feeRepository.findById(dto.FeeId);
        }

        LocalDate startDate = LocalDateMapper.StringToLocalDate(dto.EffectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(dto.ExpiryDate);

        // Create result
        entity.setAdjustmentId(dto.AdjustmentId);
        entity.setFee(fee);
        entity.setAdjustmentAmount(dto.AdjustmentAmount);
        entity.setAdjustmentType(dto.AdjustmentType);
        entity.setReason(dto.Reason);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        // Return result
        return entity;
    }
}
