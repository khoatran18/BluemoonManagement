package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.resident_fee_service.entity.Resident;
import com.project.resident_fee_service.entity.Apartment;

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
