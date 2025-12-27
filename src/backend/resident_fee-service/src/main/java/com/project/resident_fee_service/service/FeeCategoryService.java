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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class FeeCategoryService {

    private static final Logger log =
            LoggerFactory.getLogger(FeeCategoryService.class);

    @Inject
    FeeCategoryRepository feeCategoryRepository;

    FeeCategoryMapper feeCategoryMapper = new FeeCategoryMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public FeeCategoryDTO.GetFeeCategoriesResponseDTO getFeeCategoriesByFilter(
            Long feeTypeId,
            int page,
            int limit
    ) {

        log.info("[Fee] [Service] getFeeCategoriesByFilter Start");
        log.info("Input: feeTypeId={}, page={}, limit={}", feeTypeId, page, limit);

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<FeeCategory> feeCategoryEntityList =
                    feeCategoryRepository.getByFilter(
                            feeTypeId, queryPage, queryLimit
                    );

            long count =
                    feeCategoryRepository.countByFilter(feeTypeId);

            List<FeeCategoryDTO.GetFeeCategoriesResponseItemDTO> feeCategoriesDto =
                    feeCategoryMapper.GetFeeCategoriesResponseItemsEntityToDTO(
                            feeCategoryEntityList
                    );

            FeeCategoryDTO.GetFeeCategoriesResponseDTO responseData =
                    new FeeCategoryDTO.GetFeeCategoriesResponseDTO();
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.FeeCategories = feeCategoriesDto;

            log.info("[Fee] [Service] getFeeCategoriesByFilter End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeeCategoriesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public FeeCategoryDTO.GetFeeCategoryResponseDTO getFeeCategoryById(
            Long feeCategoryId
    ) {

        log.info("[Fee] [Service] getFeeCategoryById Start");
        log.info("Input: feeCategoryId={}", feeCategoryId);

        try {
            FeeCategory feeCategory =
                    feeCategoryRepository.findById(feeCategoryId);
            if (feeCategory == null)
                throw new NotFoundException(
                        "Fee Category not found with id: " + feeCategoryId
                );

            FeeCategoryDTO.GetFeeCategoryResponseDTO result =
                    feeCategoryMapper.GetFeeCategoryResponseEntityToDTO(feeCategory);

            log.info("[Fee] [Service] getFeeCategoryById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeeCategoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @Transactional
    public void createFeeCategory(
            FeeCategoryDTO.PostFeeCategoryRequestDTO dto
    ) {

        log.info("[Fee] [Service] createFeeCategory Start");
        log.info("Input: {}", dto);

        try {
            FeeCategory entity =
                    feeCategoryMapper.PostFeeCategoryRequestDTOToEntity(dto);

            feeCategoryRepository.create(entity);

            log.info("[Fee] [Service] createFeeCategory End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createFeeCategory Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @Transactional
    public void updateFeeCategoryById(
            FeeCategoryDTO.PutFeeCategoryRequestDTO dto
    ) {

        log.info("[Fee] [Service] updateFeeCategoryById Start");
        log.info("Input: {}", dto);

        try {
            FeeCategory checkedEntity =
                    feeCategoryRepository.findById(dto.FeeCategoryId);
            if (checkedEntity == null)
                throw new NotFoundException(
                        "Fee Category not found with id: " + dto.FeeCategoryId
                );

            FeeCategory entity =
                    feeCategoryMapper.PutFeeCategoryRequestDTOToEntity(dto);

            feeCategoryRepository.update(entity);

            log.info("[Fee] [Service] updateFeeCategoryById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] updateFeeCategoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @Transactional
    public void deleteFeeCategoryById(Long feeCategoryId) {

        log.info("[Fee] [Service] deleteFeeCategoryById Start");
        log.info("Input: feeCategoryId={}", feeCategoryId);

        try {
            FeeCategory checkedEntity =
                    feeCategoryRepository.findById(feeCategoryId);
            if (checkedEntity == null)
                throw new NotFoundException(
                        "Fee Category delete not found with id: " + feeCategoryId
                );

            feeCategoryRepository.delete(checkedEntity);

            log.info("[Fee] [Service] deleteFeeCategoryById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] deleteFeeCategoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
