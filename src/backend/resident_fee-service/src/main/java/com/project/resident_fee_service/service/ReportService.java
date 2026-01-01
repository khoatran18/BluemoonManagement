package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.resident_fee_service.dto.ReportDTO;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.mapper.ReportMapper;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;
import com.project.resident_fee_service.repository.PayHistoryRepository;
import com.project.resident_fee_service.repository.ResidentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@ApplicationScoped
public class ReportService {

    private static final Logger log =
            LoggerFactory.getLogger(ReportService.class);

    ReportMapper reportMapper = new ReportMapper();

    @Inject
    ApartmentRepository apartmentRepository;

    @Inject
    ResidentRepository residentRepository;

    @Inject
    FeeRepository feeRepository;

    @Inject
    PayHistoryRepository payHistoryRepository;

    public ReportDTO.ApartmentCommonReportDTO getApartmentCommonReport() {
        log.info("[Report] [Service] getApartmentCommonReport Start");
        log.info("Input: None");

        try {
            Long residentTotal = residentRepository.count();
            Long roomTotal = apartmentRepository.count();
            Long buildingTotal = apartmentRepository
                    .find("SELECT COUNT(DISTINCT a.building) FROM Apartment a")
                    .project(Long.class)
                    .firstResult();

            ReportDTO.ApartmentCommonReportDTO responseData = reportMapper.ApartmentCommonReportToDTO(residentTotal, roomTotal, buildingTotal);

            log.info("[Report] [Service] getApartmentCommonReport End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[Report] [Service] getApartmentCommonReport Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    public ReportDTO.FeeCommonReportDTO getFeeCommonReport() {
        log.info("[Report] [Service] getFeeCommonReport Start");
        log.info("Input: None");

        try {
            Long activeFeeCount = feeRepository.count("status", Fee.FeeStatus.ACTIVE);
            Long draftFeeCount = feeRepository.count("status", Fee.FeeStatus.DRAFT);
            Long closedFeeCount = feeRepository.count("status", Fee.FeeStatus.CLOSED);
            Long archivedFeeCount = feeRepository.count("status", Fee.FeeStatus.ARCHIVED);

            BigDecimal totalPaidFeeAmount = payHistoryRepository
                    .find("SELECT COALESCE(SUM(x.payAmount), 0) FROM PayHistory x")
                    .project(BigDecimal.class)
                    .firstResult();

            ReportDTO.FeeCommonReportDTO responseData = reportMapper.FeeCommonReportToDTO(
                    totalPaidFeeAmount, activeFeeCount, draftFeeCount, closedFeeCount, archivedFeeCount
            );

            log.info("[Report] [Service] getFeeCommonReport End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[Report] [Service] getFeeCommonReport Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

}
