package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.FeeDTO;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.exception.InternalServerException;
import com.project.resident_fee_service.exception.NotFoundException;
import com.project.resident_fee_service.mapper.FeeMapper;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.repository.FeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FeeService {

    @Inject
    FeeRepository repository;

    FeeMapper feeMapper = new FeeMapper();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Handle logic for API getAllFees: retrieve database and convert to response DTO
     */
    public FeeDTO.GetFeesResponseDTO getFeesByFilter(
            Long feeTypeId,
            Long feeCategoryId,
            String feeName,
            BigDecimal feeAmount,
            String applicableMonth,
            String effectiveDate,
            String expiryDate,
            String status,
            int page,
            int limit
    ) {

        // Init data for response
        FeeDTO.GetFeesResponseDTO responseData = new FeeDTO.GetFeesResponseDTO();

        // Valid and convert request params to repository params
        int queryPage = Math.max(page, 1);
        int queryLimit = Math.max(limit, 1);
        LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
        LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

        try {
            // Retrieve database
            List<Fee> feeEntityList = repository.getByFilter(
                    feeTypeId,
                    feeCategoryId,
                    feeName,
                    feeAmount,
                    applicableMonth,
                    startDate,
                    endDate,
                    status,
                    queryPage,
                    queryLimit
            );
            long count = repository.countByFilter(
                    feeTypeId,
                    feeCategoryId,
                    feeName,
                    feeAmount,
                    applicableMonth,
                    startDate,
                    endDate,
                    status
            );

            // Prepare data for response
            List<FeeDTO.GetFeesResponseItemDTO> feesDto = feeMapper.GetFeesResponseItemsEntityToDTO(feeEntityList);

            // Create data for response
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Fees = feesDto;

            // Return data for response
            return responseData;
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * Handle logic for API getFeeById: retrieve database and convert to response DTO
     */
    public FeeDTO.GetFeeResponseDTO getFeeById(long feeId) {

        try {
            // Retrieve database
            Fee fee = repository.findById(feeId);
            if (fee == null)
                throw new NotFoundException("Fee not found with id: " + feeId);

            // Return data for response
            return feeMapper.GetFeeResponseEntityToDTO(fee);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Handle logic for API createFee: convert request DTO to entity and persist to database
     */
    @Transactional
    public void createFee(FeeDTO.PostFeeRequestDTO dto) {

        // Convert to entity
        Fee entity = feeMapper.PostFeeRequestDTOToEntity(dto);

        try {
            // Persist to database
            repository.create(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Handle logic for API updateFeeById: convert request DTO to entity and update to database
     */
    @Transactional
    public void updateFeeById(FeeDTO.PutFeeRequestDTO dto) {

        try {
            // Check existed
            Fee checkedEntity = repository.findById(dto.FeeId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee not found with id: " + dto.FeeId);

            // Convert to entity
            Fee entity = feeMapper.PutFeeRequestDTOToEntity(dto);

            // Update to database
            repository.update(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Handle logic for API deleteFeeById: delete record
     */
    @Transactional
    public void deleteFeeById(Long feeId) {

        try {
            // Check existed
            Fee checkedEntity = repository.findById(feeId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee delete not found with id: " + feeId);

            // Delete in database
            repository.delete(checkedEntity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
