package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.FeeCategoryDTO;
import com.project.resident_fee_service.entity.FeeCategory;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.mapper.FeeCategoryMapper;
import com.project.resident_fee_service.repository.FeeCategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FeeCategoryService {

    @Inject
    FeeCategoryRepository feeCategoryRepository;
    FeeCategoryMapper feeCategoryMapper = new FeeCategoryMapper();

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Handle logic for API getAllCategories: retrieve database and convert to response DTO
     */
    public FeeCategoryDTO.GetFeeCategoriesResponseDTO getFeeCategoriesByFilter(
            Long feeCategoryId,
            int page,
            int limit
    ) {

        // Init data for response
        FeeCategoryDTO.GetFeeCategoriesResponseDTO responseData = new FeeCategoryDTO.GetFeeCategoriesResponseDTO();

        // Valid and convert request params to repository params
        int queryPage = Math.max(page, 1);
        int queryLimit = Math.max(limit, 1);

        try {
            // Retrieve database
            List<FeeCategory> feeCategoryEntityList = feeCategoryRepository.getByFilter(
                    feeCategoryId,
                    queryPage,
                    queryLimit);
            long count = feeCategoryRepository.countByFilter(feeCategoryId);

            // Prepare data for response
            List<FeeCategoryDTO.GetFeeCategoriesResponseItemDTO> feeCategoriesDto = feeCategoryMapper.GetFeeCategoriesResponseItemsEntityToDTO(feeCategoryEntityList);

            // Create data for response
            responseData.Page = page;
            responseData.Limit = limit;
            responseData.TotalItems = count;
            responseData.FeeCategories = feeCategoriesDto;

            // Return data for response
            return responseData;
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * Handle logic for API getFeeCategoryById: retrieve database and convert to response DTO
     */
    public FeeCategoryDTO.GetFeeCategoryResponseDTO getFeeCategoryById(Long feeCategoryId) {
        try {
            // Retrieve database
            FeeCategory feeCategory = feeCategoryRepository.findById(feeCategoryId);
            if (feeCategory == null)
                throw new NotFoundException("Fee Category not found with id: " + feeCategoryId);

            // Return data for response
            return feeCategoryMapper.GetFeeCategoryResponseEntityToDTO(feeCategory);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Handle logic for API createFeeCategory: convert request DTO to entity and persist to database
     */
    @Transactional
    public void createFeeCategory(FeeCategoryDTO.PostFeeCategoryRequestDTO dto) {

        // Convert to entity
        FeeCategory entity = feeCategoryMapper.PostFeeCategoryRequestDTOToEntity(dto);

        try {
            // Persist to database
            feeCategoryRepository.create(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Handle logic for API updateFeeCategoryById: convert request DTO to entity and update to database
     */
    @Transactional
    public void updateFeeCategoryById(FeeCategoryDTO.PutFeeCategoryRequestDTO dto) {
        try {
            // Check existed
            FeeCategory checkedEntity = feeCategoryRepository.findById(dto.FeeCategoryId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee Category not found with id: " + dto.FeeCategoryId);

            // Convert to entity
            FeeCategory entity = feeCategoryMapper.PutFeeCategoryRequestDTOToEntity(dto);

            // Update to database
            feeCategoryRepository.update(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Handle logic for API deleteFeeCategoryById: delete record
     */
    @Transactional
    public void deleteFeeCategoryById(Long feeCategoryId) {

        try {
            // Check existed
            FeeCategory checkedEntity = feeCategoryRepository.findById(feeCategoryId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee Category delete not found with id: " + feeCategoryId);

            // Delete in database
            feeCategoryRepository.delete(checkedEntity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


}
