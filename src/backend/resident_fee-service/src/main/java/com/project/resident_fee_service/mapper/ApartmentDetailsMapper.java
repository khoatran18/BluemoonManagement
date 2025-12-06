package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;

import java.util.stream.Collectors;

public class ApartmentDetailsMapper {

    public static ApartmentDetailsDTO toDTO(Apartment entity) {
        ApartmentDetailsDTO dto = new ApartmentDetailsDTO();

        dto.apartmentId = entity.getApartmentId();
        dto.building = entity.getBuilding();
        dto.roomNumber = entity.getRoomNumber();

        Resident head = entity.getHeadResident();
        dto.headResidentId = head == null ? null : head.getResidentId();

        dto.residents = entity.getResidents()
                .stream()
                .map(ApartmentDetailsMapper::mapResidentInfo)
                .collect(Collectors.toList());

        return dto;
    }

    private static ApartmentDetailsDTO.ResidentInfo mapResidentInfo(Resident r) {
        ApartmentDetailsDTO.ResidentInfo info = new ApartmentDetailsDTO.ResidentInfo();
        info.residentId = r.getResidentId();
        info.fullName = r.getFullName();
        return info;
    }
}
