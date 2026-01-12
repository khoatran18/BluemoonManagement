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

        // ===== Motors =====
        dto.totalMotor = entity.getMotorNumbers() == null
                ? 0
                : entity.getMotorNumbers().size();

        dto.motorNumbers = entity.getMotorNumbers() == null
                ? java.util.List.of()
                : entity.getMotorNumbers().stream()
                .map(ApartmentDetailsMapper::mapVehicleNumber)
                .collect(Collectors.toList());

        // ===== Cars =====
        dto.totalCar = entity.getCarNumbers() == null
                ? 0
                : entity.getCarNumbers().size();

        dto.carNumbers = entity.getCarNumbers() == null
                ? java.util.List.of()
                : entity.getCarNumbers().stream()
                .map(ApartmentDetailsMapper::mapVehicleNumber)
                .collect(Collectors.toList());


        return dto;
    }

    private static ApartmentDetailsDTO.ResidentInfo mapResidentInfo(Resident r) {
        ApartmentDetailsDTO.ResidentInfo info = new ApartmentDetailsDTO.ResidentInfo();
        info.residentId = r.getResidentId();
        info.fullName = r.getFullName();
        return info;
    }

    private static VehicleNumberDTO mapVehicleNumber(String number) {
        VehicleNumberDTO dto = new VehicleNumberDTO();
        dto.number = number;
        return dto;
    }
}
