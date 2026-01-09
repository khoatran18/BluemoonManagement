package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.DeleteFeeHistoryDTO;
import com.project.resident_fee_service.entity.DeleteFeeHistory;

import java.util.ArrayList;
import java.util.List;

public class DeleteFeeHistoryMapper {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert list of DeleteFeeHistory entity to DTO items.
     */
    public List<DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseItemDTO>
    GetDeleteFeeHistoriesResponseItemsEntityToDTO(
            List<DeleteFeeHistory> entityList) {

        List<DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseItemDTO> dtoList
                = new ArrayList<>();

        for (DeleteFeeHistory entity : entityList) {
            dtoList.add(GetDeleteFeeHistoriesResponseItemEntityToDTO(entity));
        }

        return dtoList;
    }

    /**
     * Convert DeleteFeeHistory entity to DTO item.
     */
    public DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseItemDTO
    GetDeleteFeeHistoriesResponseItemEntityToDTO(
            DeleteFeeHistory entity) {

        DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseItemDTO dto
                = new DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseItemDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.FeeID = entity.getFeeId();
        dto.FeeTypeID = entity.getFeeTypeId();
        dto.FeeCategoryID = entity.getFeeCategoryId();
        dto.FeeName = entity.getFeeName();
        dto.FeeDescription = entity.getFeeDescription();
        dto.ApplicableMonth = entity.getApplicableMonth();
        dto.Amount = entity.getAmount();
        dto.StartDate = entity.getStartDate();
        dto.EndDate = entity.getEndDate();
        dto.Status = entity.getStatus();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }

    /**
     * Convert DeleteFeeHistory entity to single DTO.
     */
    public DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO
    GetDeleteFeeHistoryResponseEntityToDTO(
            DeleteFeeHistory entity) {

        DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO dto
                = new DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.FeeID = entity.getFeeId();
        dto.FeeTypeID = entity.getFeeTypeId();
        dto.FeeCategoryID = entity.getFeeCategoryId();
        dto.FeeName = entity.getFeeName();
        dto.FeeDescription = entity.getFeeDescription();
        dto.ApplicableMonth = entity.getApplicableMonth();
        dto.Amount = entity.getAmount();
        dto.StartDate = entity.getStartDate();
        dto.EndDate = entity.getEndDate();
        dto.Status = entity.getStatus();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }
}
