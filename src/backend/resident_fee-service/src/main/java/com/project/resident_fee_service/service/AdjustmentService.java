package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.common_package.exception.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.mapper.AdjustmentMapper;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.repository.AdjustmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class AdjustmentService {

    private static final Logger log =
            LoggerFactory.getLogger(AdjustmentService.class);

    @Inject
    AdjustmentRepository repository;

    @Inject
    FeeRepository feeRepository;

    @Inject
    AdjustmentBalanceService adjustmentBalanceService;

    AdjustmentMapper adjustmentMapper = new AdjustmentMapper();

    /////////////////////////////
    // HELPER: VALIDATION
    /////////////////////////////

    /**
     * Validates that the Adjustment period falls strictly within the Fee period.
     */
    private void validateAdjustmentPeriod(Fee fee, LocalDate adjStart, LocalDate adjEnd) {
        if (fee == null) return;

        LocalDate feeStart = fee.getStartDate();
        LocalDate feeEnd = fee.getEndDate();

        // Check Start Date
        if (feeStart != null && adjStart.isBefore(feeStart)) {
            throw new BadRequestException(
                    "Adjustment Effective Date (" + adjStart +
                            ") cannot be before Fee Start Date (" + feeStart + ")."
            );
        }

        // Check End Date (only if Fee has an end date)
        if (feeEnd != null) {
            if (adjEnd.isAfter(feeEnd)) {
                throw new BadRequestException(
                        "Adjustment Expiry Date (" + adjEnd +
                                ") cannot be after Fee End Date (" + feeEnd + ")."
                );
            }
        }

        // Start <= End
        if (adjStart.isAfter(adjEnd)) {
            throw new BadRequestException("Adjustment Effective Date cannot be after Expiry Date.");
        }
    }

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public AdjustmentDTO.GetAdjustmentsResponseDTO getAdjustmentsByFilter(
            Long feeId,
            BigDecimal adjustmentAmount,
            Adjustment.AdjustmentType adjustmentType,
            String effectiveDate,
            String expiryDate,
            int page,
            int limit
    ) {
        log.info("[Fee] [Service] getAdjustmentsByFilter Start");
        log.info("Input: feeId={}, adjustmentAmount={}, adjustmentType={}, effectiveDate={}, expiryDate={}, page={}, limit={}",
                feeId, adjustmentAmount, adjustmentType, effectiveDate, expiryDate, page, limit);

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);
            LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
            LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

            List<Adjustment> adjustmentEntityList = repository.getByFilter(
                    feeId, adjustmentAmount, adjustmentType,
                    startDate, endDate, queryPage, queryLimit
            );

            long count = repository.countByFilter(
                    feeId, adjustmentAmount, adjustmentType,
                    startDate, endDate
            );

            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto =
                    adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            AdjustmentDTO.GetAdjustmentsResponseDTO responseData = new AdjustmentDTO.GetAdjustmentsResponseDTO();
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Adjustments = adjustmentsDto;

            log.info("[Fee] [Service] getAdjustmentsByFilter End");
            log.info("Output: {}", responseData);

            return responseData;
        } catch (Exception e) {
            log.error("[Fee] [Service] getAdjustmentsByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public AdjustmentDTO.GetAdjustmentResponseDTO getAdjustmentById(long adjustmentId) {
        log.info("[Fee] [Service] getAdjustmentById Start");
        log.info("Input: adjustmentId={}", adjustmentId);

        try {
            Adjustment adjustment = repository.findById(adjustmentId);
            if (adjustment == null)
                throw new NotFoundException("Adjustment not found with id: " + adjustmentId);

            AdjustmentDTO.GetAdjustmentResponseDTO result =
                    adjustmentMapper.GetAdjustmentResponseEntityToDTO(adjustment);

            log.info("[Fee] [Service] getAdjustmentById End");
            log.info("Output: {}", result);

            return result;
        } catch (Exception e) {
            log.error("[Fee] [Service] getAdjustmentById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    public AdjustmentDTO.GetAdjustmentsResponseDTO getApartmentSpecificAdjustmentsByFilter(
            BigDecimal adjustmentAmount,
            Adjustment.AdjustmentType adjustmentType,
            String effectiveDate,
            String expiryDate,
            int page,
            int limit
    ) {
        log.info("[Fee] [Service] getApartmentSpecificAdjustmentsByFilter Start");
        log.info(
                "Input: adjustmentAmount={}, adjustmentType={}, effectiveDate={}, expiryDate={}, page={}, limit={}",
                adjustmentAmount, adjustmentType,
                effectiveDate, expiryDate, page, limit
        );
        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);
            LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
            LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

            List<Adjustment> adjustmentEntityList = repository.getApartmentAdjustmentsByFilter(
                    adjustmentAmount, adjustmentType, startDate, endDate, queryPage, queryLimit
            );

            long count = repository.countApartmentAdjustmentsByFilter(
                    adjustmentAmount, adjustmentType, startDate, endDate
            );

            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto =
                    adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            AdjustmentDTO.GetAdjustmentsResponseDTO responseData = new AdjustmentDTO.GetAdjustmentsResponseDTO();
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Adjustments = adjustmentsDto;

            log.info("[Fee] [Service] getApartmentSpecificAdjustmentsByFilter End");
            log.info("Output: {}", responseData);
            return responseData;
        } catch (Exception e) {
            log.error("[Fee] [Service] getApartmentSpecificAdjustmentsByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @Transactional
    public void createAdjustment(AdjustmentDTO.PostAdjustmentRequestDTO dto) {

        log.info("[Fee] [Service] createAdjustment Start");
        log.info("Input: {}", dto);

        try {
            Adjustment entity = adjustmentMapper.PostAdjustmentRequestDTOToEntity(dto);

            // Ensure we have the full Fee entity to check dates
            Fee fee = entity.getFee();
            if (fee == null && dto.FeeId != null) {
                fee = feeRepository.findById(dto.FeeId);
                entity.setFee(fee);
            }

//            if (fee == null) {
//                throw new NotFoundException("Fee not found with id: " + dto.FeeId);
//            }

            validateAdjustmentPeriod(fee, entity.getStartDate(), entity.getEndDate());

            repository.create(entity);
            // Update balance to apartment paid fee
            if (entity.getFee() != null) {
                Set<Apartment> apartments = fee.getPaidApartmentList();

                for (Apartment apartment : apartments) {
                    ApartmentFeeStatus apartmentFeeStatus = apartment.getApartmentFeeStatus();

                    BigDecimal changeAmount =
                            (entity.getAdjustmentType() == Adjustment.AdjustmentType.decrease)
                                    ? entity.getAdjustmentAmount()
                                    : entity.getAdjustmentAmount().negate();

                    BigDecimal oldBalance = apartmentFeeStatus.getBalance();

                    apartmentFeeStatus.setBalance(
                            apartmentFeeStatus.getBalance().add(changeAmount)
                    );

                    adjustmentBalanceService.createAdjustmentBalanceLocalBackend(
                            apartment.getApartmentId(),
                            fee.getFeeId(),
                            entity.getAdjustmentId(), oldBalance,
                            apartmentFeeStatus.getBalance(),
                            "Thay đổi phí"
                    );
                }
            }

            log.info("[Fee] [Service] createAdjustment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createAdjustment Error", e);
            if (e instanceof jakarta.ws.rs.WebApplicationException) throw e;
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @Transactional
    public void updateAdjustmentById(AdjustmentDTO.PutAdjustmentRequestDTO dto) {

        log.info("[Fee] [Service] updateAdjustmentById Start");
        log.info("Input: {}", dto);

        try {
            Adjustment checkedEntity = repository.findById(dto.AdjustmentId);
            if (checkedEntity == null)
                throw new NotFoundException("Adjustment not found with id: " + dto.AdjustmentId);

            Adjustment entity = adjustmentMapper.PutAdjustmentRequestDTOToEntity(dto);

            // If the DTO didn't provide a new Fee ID, we assume the Fee is unchanged.
            // We must use the existing Fee from the DB to validate the NEW dates.
            Fee fee = entity.getFee();
            if (fee == null) {
                // Fallback to the existing fee in the DB
                fee = checkedEntity.getFee();
                entity.setFee(fee);
            }

            if (fee == null && dto.FeeId != null) {
                // Try to fetch if mapped entity missed it but DTO had it
                fee = feeRepository.findById(dto.FeeId);
                entity.setFee(fee);
            }

            if (fee != null) {
                validateAdjustmentPeriod(fee, entity.getStartDate(), entity.getEndDate());
            }

            repository.update(entity);

            log.info("[Fee] [Service] updateAdjustmentById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] updateAdjustmentById Error", e);
            if (e instanceof jakarta.ws.rs.WebApplicationException) throw e;
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @Transactional
    public void deleteAdjustmentById(Long adjustmentId) {
        log.info("[Fee] [Service] deleteAdjustmentById Start");
        log.info("Input: adjustmentId={}", adjustmentId);

        try {
            Adjustment checkedEntity = repository.findById(adjustmentId);
            if (checkedEntity == null)
                throw new NotFoundException("Adjustment delete not found with id: " + adjustmentId);

            repository.delete(checkedEntity);

            log.info("[Fee] [Service] deleteAdjustmentById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] deleteAdjustmentById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}