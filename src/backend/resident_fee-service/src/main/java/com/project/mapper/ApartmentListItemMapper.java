package com.project.mapper;

import com.project.dto.ApartmentListItemDTO;
import com.project.entity.Apartment;
import com.project.entity.Resident;

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
