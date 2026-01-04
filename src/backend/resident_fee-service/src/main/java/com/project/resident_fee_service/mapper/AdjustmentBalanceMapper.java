package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.AdjustmentBalanceDTO;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.entity.FeeType;
import com.project.resident_fee_service.entity.AdjustmentBalance;
import com.project.resident_fee_service.repository.AdjustmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;

import java.util.ArrayList;
import java.util.List;

public class AdjustmentBalanceMapper {

    FeeRepository feeRepository = new FeeRepository();
    AdjustmentRepository adjustmentRepository = new AdjustmentRepository();


    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of AdjustmentBalance entity to list of GetAdjustmentBalancesResponseItemsEntityToDTO.
     * Used when returning a list of Fee records to client.
     */
    public List<AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO> GetAdjustmentBalancesResponseItemsEntityToDTO(List<AdjustmentBalance> entityList) {

        // Init result
        List<AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO> dtoList = new ArrayList<AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO>();

        // Create result
        for (AdjustmentBalance entity : entityList) {
            AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO dto = GetAdjustmentBalancesResponseItemEntityToDTO(entity);
            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }


    /**
     * Convert a AdjustmentBalance record to a GetAdjustmentBalancesResponseItemDTO.
     * Used when returning a list of Fee records to client, called in GetAdjustmentBalancesResponseItemsEntityToDTO.
     */
    public AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO GetAdjustmentBalancesResponseItemEntityToDTO(AdjustmentBalance entity) {

        // Init result
        AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO dto = new AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO();

        // Prepare result
        Fee fee = feeRepository.findById(entity.getFeeID());
        String feeName = fee.getFeeName();
        Adjustment adjustment = adjustmentRepository.findById(entity.getAdjustmentID());

        String feeStartDatetime = LocalDateMapper.LocalDateToString(fee.getStartDate());
        String adjustmentStartDatetime = LocalDateMapper.LocalDateToString(adjustment.getStartDate());


        // Create result
        dto.AdjustmentBalanceID = entity.getAdjustmentBalanceID();
        dto.ApartmentID = entity.getApartmentID();
        dto.FeeID = entity.getFeeID();
        dto.AdjustmentID = entity.getAdjustmentID();
        dto.FeeName = feeName;
        dto.AdjustmentReason = adjustment.getReason();
        dto.OldBalance = entity.getOldBalance();
        dto.NewBalance = entity.getNewBalance();
        dto.FeeStartDatetime = feeStartDatetime;
        dto.AdjustmentStartDatetime = adjustmentStartDatetime;
        dto.AdjustmentBalanceNote = entity.getAdjustmentBalanceNote();

        // Return result
        return dto;
    }


    /**
     * Convert AdjustmentBalance entity to GetFeeResponseDTO.
     * Used when returning a Fee record to client.
     */
    public AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO GetAdjustmentBalanceResponseEntityToDTO(AdjustmentBalance entity) {

        // Init result
        AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO dto = new AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO();

        // Prepare result
        Fee fee = feeRepository.findById(entity.getFeeID());
        String feeName = fee.getFeeName();
        Adjustment adjustment = adjustmentRepository.findById(entity.getAdjustmentID());

        String feeStartDatetime = LocalDateMapper.LocalDateToString(fee.getStartDate());
        String adjustmentStartDatetime = LocalDateMapper.LocalDateToString(adjustment.getStartDate());

        // Create result
        dto.AdjustmentBalanceID = entity.getAdjustmentBalanceID();
        dto.ApartmentID = entity.getApartmentID();
        dto.FeeID = entity.getFeeID();
        dto.AdjustmentID = entity.getAdjustmentID();
        dto.FeeName = feeName;
        dto.AdjustmentReason = adjustment.getReason();
        dto.OldBalance = entity.getOldBalance();
        dto.NewBalance = entity.getNewBalance();
        dto.FeeStartDatetime = feeStartDatetime;
        dto.AdjustmentStartDatetime = adjustmentStartDatetime;
        dto.AdjustmentBalanceNote = entity.getAdjustmentBalanceNote();

        // Return result
        return dto;
    }

}
