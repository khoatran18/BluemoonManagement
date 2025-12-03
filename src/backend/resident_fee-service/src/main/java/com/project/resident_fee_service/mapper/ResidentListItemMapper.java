package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.resident_fee_service.entity.Resident;
import com.project.resident_fee_service.entity.Apartment;

public class ResidentListItemMapper {

    public static ResidentListItemDTO toDTO(Resident entity) {
        if (entity == null) return null;
        ResidentListItemDTO dto = new ResidentListItemDTO();

        dto.residentId = entity.getResidentId();
        dto.fullName = entity.getFullName();
        dto.email = entity.getEmail();
        dto.phoneNumber = entity.getPhoneNumber();
        dto.isHead = entity.getIsHead();

        Apartment apt = entity.getApartment();
        if (apt != null) {
            ResidentListItemDTO.ApartmentInfo aptDTO = new ResidentListItemDTO.ApartmentInfo();
            aptDTO.id = apt.getApartmentId();
            aptDTO.building = apt.getBuilding();
            aptDTO.roomNumber = apt.getRoomNumber();
            dto.apartment = aptDTO;
        }

        return dto;
    }
}
