package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;

import java.util.List;
public class ApartmentMutationMapper {

    public static Apartment toEntity(ApartmentCreateDTO dto) {
        if (dto == null) return null;
        Apartment entity = new Apartment();
        entity.setBuilding(dto.building);
        entity.setRoomNumber(dto.roomNumber);
        return entity;
    }

    public static void updateEntity(Apartment entity, ApartmentUpdateDTO dto, Resident headResident, List<Resident> residents) {
        if (entity == null || dto == null) return;
        // apply updates only when DTO fields are non-null to avoid accidental overwrite
        if (dto.building != null) {
            entity.setBuilding(dto.building);
        }
        if (dto.roomNumber != null) {
            entity.setRoomNumber(dto.roomNumber);
        }
        // headResident may be null intentionally (clear head)
        entity.setHeadResident(headResident);

        if (dto.residents != null) {
            entity.setResidents(residents);
        }

        // Attach apartment to resident
        if (residents != null) {
            for (Resident r : residents) {
                r.setApartment(entity);
            }
        }
    }
}
