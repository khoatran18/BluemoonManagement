package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;

import java.util.List;

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

        // ===== Motors =====
        dto.totalMotor = entity.getMotorNumbers() == null
                ? 0
                : entity.getMotorNumbers().size();

        dto.motorNumbers = entity.getMotorNumbers() == null
                ? List.of()
                : entity.getMotorNumbers().stream()
                .map(ApartmentListItemMapper::mapVehicleNumber)
                .toList();

        // ===== Cars =====
        dto.totalCar = entity.getCarNumbers() == null
                ? 0
                : entity.getCarNumbers().size();

        dto.carNumbers = entity.getCarNumbers() == null
                ? List.of()
                : entity.getCarNumbers().stream()
                .map(ApartmentListItemMapper::mapVehicleNumber)
                .toList();

        return dto;
    }

    private static VehicleNumberDTO mapVehicleNumber(String number) {
        VehicleNumberDTO dto = new VehicleNumberDTO();
        dto.number = number;
        return dto;
    }
}
