package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.FeeDTO;
import com.project.resident_fee_service.entity.Fee;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.mapper.FeeMapper;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FeeService {

    private static final Logger log =
            LoggerFactory.getLogger(FeeService.class);

    @Inject
    EntityManager entityManager;

    @Inject
    FeeRepository repository;

    @Inject
    ApartmentRepository apartmentRepository;

    FeeMapper feeMapper = new FeeMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public FeeDTO.GetFeesResponseDTO getFeesByFilter(
            List<Long> feeTypeIds,
            Long feeCategoryId,
            String feeName,
            BigDecimal feeAmount,
            String applicableMonth,
            String effectiveDate,
            String expiryDate,
            Fee.FeeStatus status,
            int page,
            int limit
    ) {

        log.info("[Fee] [Service] getFeesByFilter Start");
        log.info(
                "Input: feeTypeId={}, feeCategoryId={}, feeName={}, feeAmount={}, applicableMonth={}, effectiveDate={}, expiryDate={}, status={}, page={}, limit={}",
                feeTypeIds, feeCategoryId, feeName, feeAmount,
                applicableMonth, effectiveDate, expiryDate, status, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);
            LocalDate startDate = LocalDateMapper.StringToLocalDate(effectiveDate);
            LocalDate endDate = LocalDateMapper.StringToLocalDate(expiryDate);

            List<Fee> feeEntityList =
                    repository.getByFilter(
                            feeTypeIds, feeCategoryId, feeName, feeAmount,
                            applicableMonth, startDate, endDate,
                            status, queryPage, queryLimit
                    );

            long count =
                    repository.countByFilter(
                            feeTypeIds, feeCategoryId, feeName, feeAmount,
                            applicableMonth, startDate, endDate, status
                    );

            List<FeeDTO.GetFeesResponseItemDTO> feesDto =
                    feeMapper.GetFeesResponseItemsEntityToDTO(feeEntityList);

            FeeDTO.GetFeesResponseDTO responseData =
                    new FeeDTO.GetFeesResponseDTO();
            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.Fees = feesDto;

            log.info("[Fee] [Service] getFeesByFilter End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public FeeDTO.GetFeeResponseDTO getFeeById(long feeId) {

        log.info("[Fee] [Service] getFeeById Start");
        log.info("Input: feeId={}", feeId);

        try {
            Fee fee = repository.findById(feeId);
            if (fee == null)
                throw new NotFoundException("Fee not found with id: " + feeId);

            FeeDTO.GetFeeResponseDTO result =
                    feeMapper.GetFeeResponseEntityToDTO(fee);

            log.info("[Fee] [Service] getFeeById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Fee] [Service] getFeeById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @Transactional
    public void createFee(FeeDTO.PostFeeRequestDTO dto) {

        log.info("[Fee] [Service] createFee Start");
        log.info("Input: {}", dto);

        try {
            Fee entity = feeMapper.PostFeeRequestDTOToEntity(dto);
            repository.create(entity);

            entityManager.flush();

            String sql = """
                   INSERT INTO ApartmentFeeStatus_UnpaidFees (ApartmentID, FeeID)
                   SELECT afs.ApartmentID, :newFeeID
                   FROM ApartmentFeeStatus afs
                   """;

            int rowsEffected = entityManager.createNativeQuery(sql)
                    .setParameter("newFeeID", entity.getFeeId())
                    .executeUpdate();

//            List<Apartment> apartments = apartmentRepository.getAll();
//            for (Apartment apartment : apartments) {
//                ApartmentFeeStatus afs = apartment.getApartmentFeeStatus();
//                if (afs.getUnpaidFeeList() == null)
//                    afs.setUnpaidFeeList(new HashSet<>());
//
//                afs.getUnpaidFeeList().add(entity);
//            }

            log.info("[Fee] [Service] createFee End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createFee Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @Transactional
    public void updateFeeById(FeeDTO.PutFeeRequestDTO dto) {

        log.info("[Fee] [Service] updateFeeById Start");
        log.info("Input: {}", dto);

        try {
            Fee checkedEntity = repository.findById(dto.FeeId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee not found with id: " + dto.FeeId);

            Fee entity = feeMapper.PutFeeRequestDTOToEntity(dto);
            repository.update(entity);

            log.info("[Fee] [Service] updateFeeById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] updateFeeById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @Transactional
    public void deleteFeeById(Long feeId) {

        log.info("[Fee] [Service] deleteFeeById Start");
        log.info("Input: feeId={}", feeId);

        try {
            Fee checkedEntity = repository.findById(feeId);
            if (checkedEntity == null)
                throw new NotFoundException("Fee delete not found with id: " + feeId);

            repository.delete(checkedEntity);

            log.info("[Fee] [Service] deleteFeeById End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] deleteFeeById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
