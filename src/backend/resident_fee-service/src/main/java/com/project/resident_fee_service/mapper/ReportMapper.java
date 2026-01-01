package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ReportDTO;

import java.math.BigDecimal;

public class ReportMapper {

    public ReportDTO.ApartmentCommonReportDTO ApartmentCommonReportToDTO(
            Long residentTotal,
            Long roomTotal,
            Long buildingTotal
    ) {
        ReportDTO.ApartmentCommonReportDTO dto = new ReportDTO.ApartmentCommonReportDTO();
        dto.ResidentTotal = residentTotal;
        dto.RoomTotal = roomTotal;
        dto.BuildingTotal = buildingTotal;

        return dto;
    }

    public ReportDTO.FeeCommonReportDTO FeeCommonReportToDTO (
        BigDecimal totalPaidFeeAmount,
        Long activeFeeCount,
        Long draftFeeCount,
        Long closedFeeCount,
        Long archivedFeeCount
    ) {
        ReportDTO.FeeCommonReportDTO dto = new ReportDTO.FeeCommonReportDTO();

        dto.TotalPaidFeeAmount = totalPaidFeeAmount;
        dto.ActiveFeeCount = activeFeeCount;
        dto.DraftFeeCount = draftFeeCount;
        dto.ClosedFeeCount = closedFeeCount;
        dto.ArchivedFeeCount = archivedFeeCount;

        return dto;
    }

}
