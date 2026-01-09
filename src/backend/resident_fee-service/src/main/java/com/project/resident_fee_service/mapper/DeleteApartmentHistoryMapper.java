package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.DeleteApartmentHistoryDTO;
import com.project.resident_fee_service.entity.DeleteApartmentHistory;

import java.util.ArrayList;
import java.util.List;

public class DeleteApartmentHistoryMapper {

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Convert list of DeleteApartmentHistory entity to DTO items.
     */
    public List<DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseItemDTO>
    GetDeleteApartmentHistoriesResponseItemsEntityToDTO(
            List<DeleteApartmentHistory> entityList) {

        List<DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseItemDTO> dtoList
                = new ArrayList<>();

        for (DeleteApartmentHistory entity : entityList) {
            dtoList.add(GetDeleteApartmentHistoriesResponseItemEntityToDTO(entity));
        }

        return dtoList;
    }

    /**
     * Convert DeleteApartmentHistory entity to DTO item.
     */
    public DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseItemDTO
    GetDeleteApartmentHistoriesResponseItemEntityToDTO(
            DeleteApartmentHistory entity) {

        DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseItemDTO dto
                = new DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseItemDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.ApartmentID = entity.getApartmentId();
        dto.Building = entity.getBuilding();
        dto.RoomNumber = entity.getRoomNumber();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }

    /**
     * Convert DeleteApartmentHistory entity to single DTO.
     */
    public DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO
    GetDeleteApartmentHistoryResponseEntityToDTO(
            DeleteApartmentHistory entity) {

        DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO dto
                = new DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO();

        dto.HistoryID = entity.getHistoryId();
        dto.ApartmentID = entity.getApartmentId();
        dto.Building = entity.getBuilding();
        dto.RoomNumber = entity.getRoomNumber();
        dto.DeletedAt = entity.getDeletedAt();

        return dto;
    }
}
