package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.DeleteResidentHistoryDTO;
import com.project.resident_fee_service.entity.DeleteResidentHistory;

import java.util.ArrayList;
import java.util.List;

public class DeleteResidentHistoryMapper {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert list of DeleteResidentHistory entity to DTO items.
     */
    public List<DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseItemDTO>
    GetDeleteResidentHistoriesResponseItemsEntityToDTO(
            List<DeleteResidentHistory> entityList) {

        List<DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseItemDTO> dtoList
                = new ArrayList<>();

        for (DeleteResidentHistory entity : entityList) {
            dtoList.add(GetDeleteResidentHistoriesResponseItemEntityToDTO(entity));
        }

        return dtoList;
    }

    /**
     * Convert DeleteResidentHistory entity to DTO item.
     */
    public DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseItemDTO
    GetDeleteResidentHistoriesResponseItemEntityToDTO(
            DeleteResidentHistory entity) {

        DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseItemDTO dto
                = new DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseItemDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.ResidentID = entity.getResidentId();
        dto.ApartmentID = entity.getApartmentId();
        dto.FullName = entity.getFullName();
        dto.PhoneNumber = entity.getPhoneNumber();
        dto.Email = entity.getEmail();
        dto.IsHead = entity.getIsHead();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }

    /**
     * Convert DeleteResidentHistory entity to single DTO.
     */
    public DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO
    GetDeleteResidentHistoryResponseEntityToDTO(
            DeleteResidentHistory entity) {

        DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO dto
                = new DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.ResidentID = entity.getResidentId();
        dto.ApartmentID = entity.getApartmentId();
        dto.FullName = entity.getFullName();
        dto.PhoneNumber = entity.getPhoneNumber();
        dto.Email = entity.getEmail();
        dto.IsHead = entity.getIsHead();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }
}
