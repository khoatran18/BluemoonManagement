package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.FeeTypeDTO;
import com.project.resident_fee_service.entity.FeeType;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.mapper.FeeTypeMapper;
import com.project.resident_fee_service.repository.FeeTypeRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class FeeTypeService {

    private static final Logger log =
            LoggerFactory.getLogger(FeeTypeService.class);

    @Inject
    FeeTypeRepository feeTypeRepository;

    FeeTypeMapper feeTypeMapper = new FeeTypeMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public FeeTypeDTO.GetFeeTypesResponseDTO getFeeTypesByFilter() {

        log.info("[Fee] [Service] getFeeTypesByFilter Start");
        log.info("Input: None");

        try {
            List<FeeType> entityList =
                    feeTypeRepository.getAll();

            List<FeeTypeDTO.GetFeeTypesResponseItemDTO> dtoList =
                    feeTypeMapper.GetFeeTypesResponseItemsEntityToDTO(entityList);

            FeeTypeDTO.GetFeeTypesResponseDTO responseData =
                    new FeeTypeDTO.GetFeeTypesResponseDTO();
            responseData.FeeTypes = dtoList;

            log.info("[Fee] [Service] getFeeTypesByFilter End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeeTypesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public FeeTypeDTO.GetFeeTypeResponseDTO getFeeTypeById(Long feeTypeId) {

        log.info("[Fee] [Service] getFeeTypeById Start");
        log.info("Input: feeTypeId={}", feeTypeId);

        try {
            FeeType feeType =
                    feeTypeRepository.findById(feeTypeId);
            if (feeType == null)
                throw new NotFoundException(
                        "Fee Type not found with id: " + feeTypeId
                );

            FeeTypeDTO.GetFeeTypeResponseDTO result =
                    feeTypeMapper.GetFeeTypeResponseEntityToDTO(feeType);

            log.info("[Fee] [Service] getFeeTypeById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeeTypeById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
