package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.resident_fee_service.entity.Resident;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResidentListResponseMapper {

    public static ResidentListResponseDTO toDTO(
            int page,
            int limit,
            long totalItems,
            List<Resident> residents
    ) {
        ResidentListResponseDTO dto = new ResidentListResponseDTO();

        dto.page = page;
        dto.limit = limit;
        dto.totalItems = totalItems;

        dto.items = (residents == null)
                ? Collections.emptyList()
                : residents.stream()
                .map(ResidentListItemMapper::toDTO)
                .collect(Collectors.toList());

        return dto;
    }
}
