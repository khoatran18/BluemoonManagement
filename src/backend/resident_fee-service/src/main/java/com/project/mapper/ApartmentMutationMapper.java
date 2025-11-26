package com.project.mapper;

import com.project.dto.ApartmentDTO.*;
import com.project.entity.Apartment;
import com.project.entity.Resident;

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
    }
}
