package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;

public class ApartmentListItemMapper {

    public static ApartmentListItemDTO toDTO(Apartment entity) {
        ApartmentListItemDTO dto = new ApartmentListItemDTO();

        dto.apartmentId = entity.getApartmentId();
        dto.building = entity.getBuilding();
        dto.roomNumber = entity.getRoomNumber();

        Resident head = entity.getHeadResident();
        if (head != null) {
            ApartmentListItemDTO.HeadResident headDTO = new ApartmentListItemDTO.HeadResident();
            headDTO.id = head.getResidentId();
            headDTO.fullName = head.getFullName();
            headDTO.phone = head.getPhoneNumber();
            dto.headResident = headDTO;
        }

        return dto;
    }
}
