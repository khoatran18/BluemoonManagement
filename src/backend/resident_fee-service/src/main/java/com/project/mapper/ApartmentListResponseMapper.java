package com.project.mapper;

import com.project.dto.ApartmentListResponseDTO;
import com.project.entity.Apartment;

import java.util.List;
import java.util.stream.Collectors;

public class ApartmentListResponseMapper {

    public static ApartmentListResponseDTO toDTO(
            int page,
            int limit,
            int totalItems,
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
