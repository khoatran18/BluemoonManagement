package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.exception.InternalServerException;
import com.project.resident_fee_service.exception.NotFoundException;
import com.project.resident_fee_service.mapper.AdjustmentMapper;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.repository.AdjustmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AdjustmentService {

    @Inject
    AdjustmentRepository repository;

    AdjustmentMapper adjustmentMapper = new AdjustmentMapper();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Handle logic for API getAllAdjustments: retrieve database and convert to response DTO
     */
    public AdjustmentDTO.GetAdjustmentsResponseDTO getAdjustmentsByFilter(
            Long feeId,
            BigDecimal adjustmentAmount,
            Adjustment.AdjustmentType adjustmentType,
            String effectiveDate,
            String expiryDate,
            int page,
            int limit
    ) {

        // Init data for response
        AdjustmentDTO.GetAdjustmentsResponseDTO responseData = new AdjustmentDTO.GetAdjustmentsResponseDTO();

        // Valid and convert request params to repository params
        int queryPage = Math.max(page, 1);
        int queryLimit = Math.max(limit, 1);
        LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

        try {
            // Retrieve database
            List<Adjustment> adjustmentEntityList = repository.getByFilter(
                    feeId,
                    adjustmentAmount,
                    adjustmentType,
                    startDate,
                    endDate,
                    queryPage,
                    queryLimit
            );
            long count = repository.countByFilter(
                    feeId,
                    adjustmentAmount,
                    adjustmentType,
                    startDate,
                    endDate
            );

            // Prepare data for response
            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto = adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            // Create data for response
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Adjustments = adjustmentsDto;

            // Return data for response
            return responseData;
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * Handle logic for API getAdjustmentById: retrieve database and convert to response DTO
     */
    public AdjustmentDTO.GetAdjustmentResponseDTO getAdjustmentById(long adjustmentId) {

        try {
            // Retrieve database
            Adjustment adjustment = repository.findById(adjustmentId);
            if (adjustment == null)
                throw new NotFoundException("Adjustment not found with id: " + adjustmentId);

            // Return data for response
            return adjustmentMapper.GetAdjustmentResponseEntityToDTO(adjustment);
        } catch (Exception e) {
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
        // Init data for response
        AdjustmentDTO.GetAdjustmentsResponseDTO responseData = new AdjustmentDTO.GetAdjustmentsResponseDTO();

        // Valid and convert request params to repository params
        int queryPage = Math.max(page, 1);
        int queryLimit = Math.max(limit, 1);
        LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

        try {
            // Retrieve database
            List<Adjustment> adjustmentEntityList = repository.getApartmentAdjustmentsByFilter(
                    adjustmentAmount,
                    adjustmentType,
                    startDate,
                    endDate,
                    queryPage,
                    queryLimit
            );
            long count = repository.countApartmentAdjustmentsByFilter(
                    adjustmentAmount,
                    adjustmentType,
                    startDate,
                    endDate
            );

            // Prepare data for response
            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentsDto = adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentEntityList);

            // Create data for response
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Adjustments = adjustmentsDto;

            // Return data for response
            return responseData;
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Handle logic for API createAdjustment: convert request DTO to entity and persist to database
     */
    @Transactional
    public void createAdjustment(AdjustmentDTO.PostAdjustmentRequestDTO dto) {

        // Convert to entity
        Adjustment entity = adjustmentMapper.PostAdjustmentRequestDTOToEntity(dto);

        try {
            // Persist to database
            repository.create(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Handle logic for API updateAdjustmentById: convert request DTO to entity and update to database
     */
    @Transactional
    public void updateAdjustmentById(AdjustmentDTO.PutAdjustmentRequestDTO dto) {

        try {
            // Check existed
            Adjustment checkedEntity = repository.findById(dto.AdjustmentId);
            if (checkedEntity == null)
                throw new NotFoundException("Adjustment not found with id: " + dto.AdjustmentId);

            // Convert to entity
            Adjustment entity = adjustmentMapper.PutAdjustmentRequestDTOToEntity(dto);

            // Update to database
            repository.update(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Handle logic for API deleteAdjustmentById: delete record
     */
    @Transactional
    public void deleteAdjustmentById(Long adjustmentId) {

        try {
            // Check existed
            Adjustment checkedEntity = repository.findById(adjustmentId);
            if (checkedEntity == null)
                throw new NotFoundException("Adjustment delete not found with id: " + adjustmentId);

            // Delete in database
            repository.delete(checkedEntity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
