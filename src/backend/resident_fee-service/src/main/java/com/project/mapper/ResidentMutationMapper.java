package com.project.mapper;

import com.project.dto.ResidentCreateDTO;
import com.project.dto.ResidentUpdateDTO;
import com.project.entity.Resident;
import com.project.entity.Apartment;

public class ResidentMutationMapper {

    public static Resident toEntity(ResidentCreateDTO dto, Apartment apartment) {
        if (dto == null) return null;

        Resident entity = new Resident();
        entity.setFullName(dto.fullName);
        entity.setEmail(dto.email);
        entity.setPhoneNumber(dto.phoneNumber);
        entity.setApartment(apartment);

        return entity;
    }

    public static void updateEntity(Resident entity, ResidentUpdateDTO dto) {
        if (entity == null || dto == null) return;

        if (dto.fullName != null) {
            entity.setFullName(dto.fullName);
        }
        if (dto.email != null) {
            entity.setEmail(dto.email);
        }
        if (dto.phoneNumber != null) {
            entity.setPhoneNumber(dto.phoneNumber);
        }
    }
}
