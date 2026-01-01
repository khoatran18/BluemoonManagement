package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.FeeDTO;
import com.project.resident_fee_service.dto.PayHistoryDTO;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.entity.FeeCategory;
import com.project.resident_fee_service.entity.FeeType;
import com.project.resident_fee_service.entity.PayHistory;
import com.project.resident_fee_service.repository.FeeCategoryRepository;
import com.project.resident_fee_service.repository.FeeRepository;
import com.project.resident_fee_service.repository.FeeTypeRepository;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class PayHistoryMapper {

    FeeRepository feeRepository = new FeeRepository();


    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert a list of PayHistory entity to list of GetPayHistoriesResponseItemsEntityToDTO.
     * Used when returning a list of Fee records to client.
     */
    public List<PayHistoryDTO.GetPayHistoriesResponseItemDTO> GetPayHistoriesResponseItemsEntityToDTO(List<PayHistory> entityList) {

        // Init result
        List<PayHistoryDTO.GetPayHistoriesResponseItemDTO> dtoList = new ArrayList<PayHistoryDTO.GetPayHistoriesResponseItemDTO>();

        // Create result
        for (PayHistory entity : entityList) {
            PayHistoryDTO.GetPayHistoriesResponseItemDTO dto = GetPayHistoriesResponseItemEntityToDTO(entity);
            dtoList.add(dto);
        }

        // Return result
        return dtoList;
    }


    /**
     * Convert a PayHistory record to a GetPayHistoriesResponseItemDTO.
     * Used when returning a list of Fee records to client, called in GetPayHistoriesResponseItemsEntityToDTO.
     */
    public PayHistoryDTO.GetPayHistoriesResponseItemDTO GetPayHistoriesResponseItemEntityToDTO(PayHistory entity) {

        // Init result
        PayHistoryDTO.GetPayHistoriesResponseItemDTO dto = new PayHistoryDTO.GetPayHistoriesResponseItemDTO();

        // Prepare result
        String payDatetime = LocalDateMapper.LocalDateToString(entity.getPayDateTime());
        Fee fee = feeRepository.findById(entity.getFeeID());
        String feeName = fee.getFeeName();
        FeeType.FeeTypeName feeTypeName = fee.getFeeType().getFeeTypeName();
        String feeCategoryName = fee.getFeeCategory().getFeeCategoryName();


        // Create result
        dto.PayHistoryID = entity.getPayHistoryID();
        dto.ApartmentID = entity.getApartmentID();
        dto.FeeID = entity.getFeeID();
        dto.FeeName = feeName;
        dto.FeeTypeName = feeTypeName;
        dto.FeeCategoryName = feeCategoryName;
        dto.PayDatetime = payDatetime;
        dto.PayAmount = entity.getPayAmount();
        dto.PayNote = entity.getPayNote();

        // Return result
        return dto;
    }


    /**
     * Convert PayHistory entity to GetFeeResponseDTO.
     * Used when returning a Fee record to client.
     */
    public PayHistoryDTO.GetPayHistoryResponseDTO GetPayHistoryResponseEntityToDTO(PayHistory entity) {

        // Init result
        PayHistoryDTO.GetPayHistoryResponseDTO dto = new PayHistoryDTO.GetPayHistoryResponseDTO();

        // Prepare result
        String payDatetime = LocalDateMapper.LocalDateToString(entity.getPayDateTime());
        Fee fee = feeRepository.findById(entity.getFeeID());
        String feeName = fee.getFeeName();
        FeeType.FeeTypeName feeTypeName = fee.getFeeType().getFeeTypeName();
        String feeCategoryName = fee.getFeeCategory().getFeeCategoryName();

        // Create result
        dto.PayHistoryID = entity.getPayHistoryID();
        dto.ApartmentID = entity.getApartmentID();
        dto.FeeID = entity.getFeeID();
        dto.FeeName = feeName;
        dto.FeeTypeName = feeTypeName;
        dto.FeeCategoryName = feeCategoryName;
        dto.PayDatetime = payDatetime;
        dto.PayAmount = entity.getPayAmount();
        dto.PayNote = entity.getPayNote();

        // Return result
        return dto;
    }


}
