package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Apartment;

import java.util.List;
import java.util.stream.Collectors;

public class ApartmentListResponseMapper {

    public static ApartmentListResponseDTO toDTO(
            int page,
            int limit,
            long totalItems,
            List<Apartment> apartments
    ) {
        ApartmentListResponseDTO dto = new ApartmentListResponseDTO();

        dto.page = page;
        dto.limit = limit;
        dto.totalItems = totalItems;

        dto.items = apartments.stream()
                .map(ApartmentListItemMapper::toDTO)
                .collect(Collectors.toList());

        return dto;
    }
}
