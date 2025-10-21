package com.project.mapper;

import com.project.dto.ApartmentDTO;
import com.project.entity.Apartment;

public class ApartmentMapper {
    public static Apartment toEntity(ApartmentDTO dto) {
        Apartment entity = new Apartment();
        entity.setBuilding(dto.building);
        entity.setRoomNumber(dto.roomNumber);
        return entity;
    }

    public static ApartmentDTO toDTO(Apartment entity) {
        ApartmentDTO dto = new ApartmentDTO();
        dto.id = entity.getId();
        dto.building = entity.getBuilding();
        dto.roomNumber = entity.getRoomNumber();
        return dto;
    }
}
