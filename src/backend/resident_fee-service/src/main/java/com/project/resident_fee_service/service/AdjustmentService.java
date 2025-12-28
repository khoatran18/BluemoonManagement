package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.mapper.AdjustmentMapper;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.repository.AdjustmentRepository;

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

    AdjustmentMapper adjustmentMapper = new AdjustmentMapper();

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
        log.info(
                "Input: feeId={}, adjustmentAmount={}, adjustmentType={}, effectiveDate={}, expiryDate={}, page={}, limit={}",
                feeId, adjustmentAmount, adjustmentType,
                effectiveDate, expiryDate, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);
            LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
            LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

            List<Adjustment> adjustmentEntityList =
                    repository.getByFilter(
                            feeId,
                            adjustmentAmount,
                            adjustmentType,
                            startDate,
                            endDate,
                            queryPage,
                            queryLimit
                    );

            long count =
                    repository.countByFilter(
                            feeId,
                            adjustmentAmount,
                            adjustmentType,
                            startDate,
                            endDate
                    );

            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto =
                    adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            AdjustmentDTO.GetAdjustmentsResponseDTO responseData =
                    new AdjustmentDTO.GetAdjustmentsResponseDTO();
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

            List<Adjustment> adjustmentEntityList =
                    repository.getApartmentAdjustmentsByFilter(
                            adjustmentAmount,
                            adjustmentType,
                            startDate,
                            endDate,
                            queryPage,
                            queryLimit
                    );

            long count =
                    repository.countApartmentAdjustmentsByFilter(
                            adjustmentAmount,
                            adjustmentType,
                            startDate,
                            endDate
                    );

            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto =
                    adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            AdjustmentDTO.GetAdjustmentsResponseDTO responseData =
                    new AdjustmentDTO.GetAdjustmentsResponseDTO();
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
            Adjustment entity =
                    adjustmentMapper.PostAdjustmentRequestDTOToEntity(dto);

            repository.create(entity);

            if (entity.getFee() != null) {
                Fee fee = entity.getFee();
                Set<Apartment> apartments = fee.getPaidApartmentList();

                for (Apartment apartment : apartments) {
                    ApartmentFeeStatus apartmentFeeStatus =
                            apartment.getApartmentFeeStatus();

                    BigDecimal changeAmount =
                            (entity.getAdjustmentType() == Adjustment.AdjustmentType.decrease)
                                    ? entity.getAdjustmentAmount()
                                    : entity.getAdjustmentAmount().negate();

                    apartmentFeeStatus.setBalance(
                            apartmentFeeStatus.getBalance().add(changeAmount)
                    );
                }
            }

            log.info("[Fee] [Service] createAdjustment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createAdjustment Error", e);
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

            Adjustment entity =
                    adjustmentMapper.PutAdjustmentRequestDTOToEntity(dto);

            repository.update(entity);

            log.info("[Fee] [Service] updateAdjustmentById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] updateAdjustmentById Error", e);
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
