package com.project.mapper;

import com.project.dto.ResidentDTO.*;
import com.project.entity.Resident;
import com.project.entity.Apartment;

public class ResidentDetailsMapper {

    public static ResidentDetailsDTO toDTO(Resident entity) {
        if (entity == null) return null;

        ResidentDetailsDTO dto = new ResidentDetailsDTO();

        dto.residentId = entity.getResidentId();
        dto.fullName = entity.getFullName();
        dto.email = entity.getEmail();
        dto.phoneNumber = entity.getPhoneNumber();
        dto.isHead = entity.getIsHead();

        Apartment apt = entity.getApartment();
        if (apt != null) {
            ResidentDetailsDTO.ApartmentInfo aptDTO = new ResidentDetailsDTO.ApartmentInfo();
            aptDTO.id = apt.getApartmentId();
            aptDTO.building = apt.getBuilding();
            aptDTO.roomNumber = apt.getRoomNumber();
            dto.apartment = aptDTO;
        }

        return dto;
    }
}
