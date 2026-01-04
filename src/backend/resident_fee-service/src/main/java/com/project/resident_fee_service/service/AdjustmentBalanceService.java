package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.AdjustmentBalanceDTO;
import com.project.resident_fee_service.entity.AdjustmentBalance;
import com.project.resident_fee_service.mapper.AdjustmentBalanceMapper;
import com.project.resident_fee_service.repository.AdjustmentBalanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class AdjustmentBalanceService {

    private static final Logger log =
            LoggerFactory.getLogger(AdjustmentBalanceService.class);

    AdjustmentBalanceRepository adjustmentBalanceRepository = new AdjustmentBalanceRepository();


    AdjustmentBalanceMapper adjustmentBalanceMapper = new AdjustmentBalanceMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////


    public AdjustmentBalanceDTO.GetAdjustmentBalancesResponseDTO getAdjustmentBalancesByFilter(
            Long apartmentID,
            Long feeID,
            Long adjustmentID,
            int page,
            int limit
    ) {

        log.info("[AdjustmentBalance] [Service] getAdjustmentBalancesByFilter Start");
        log.info(
                "Input: apartmentID={}, feeID={}, page={}, limit={}",
                apartmentID, feeID, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<AdjustmentBalance> feeEntityList =
                    adjustmentBalanceRepository.getByFilter(
                            apartmentID, feeID, adjustmentID, queryPage, queryLimit
                    );

            long count =
                    adjustmentBalanceRepository.countByFilter(
                            apartmentID, feeID, adjustmentID
                    );

            List<AdjustmentBalanceDTO.GetAdjustmentBalancesResponseItemDTO> adjustmentBalancesDTO =
                    adjustmentBalanceMapper.GetAdjustmentBalancesResponseItemsEntityToDTO(feeEntityList);

            AdjustmentBalanceDTO.GetAdjustmentBalancesResponseDTO responseData =
                    new AdjustmentBalanceDTO.GetAdjustmentBalancesResponseDTO();

            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.AdjustmentBalances = adjustmentBalancesDTO;

            log.info("[AdjustmentBalance] [Service] getAdjustmentBalancesByFilter End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[AdjustmentBalance] [Service] getAdjustmentBalancesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }

    }


    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO getAdjustmentBalanceById(long payHistoryID) {

        log.info("[AdjustmentBalance] [Service] getAdjustmentBalanceById Start");
        log.info("Input: payHistoryID={}", payHistoryID);

        try {
            AdjustmentBalance payHistory = adjustmentBalanceRepository.findById(payHistoryID);
            if (payHistory == null)
                throw new NotFoundException("AdjustmentBalance not found with id: " + payHistoryID);



            AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO result =
                    adjustmentBalanceMapper.GetAdjustmentBalanceResponseEntityToDTO(payHistory);

            log.info("[AdjustmentBalance] [Service] getAdjustmentBalanceById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[AdjustmentBalance] [Service] getAdjustmentBalanceById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    /////////////////////////////
    // POST LOCAL BACKEND
    /////////////////////////////

    @Transactional
    public void createAdjustmentBalanceLocalBackend(
            Long apartmentID,
            Long feeID,
            Long adjustmentID,
            BigDecimal oldBalance,
            BigDecimal newBalance,
            String adjustmentBalanceNote
    ) {

        log.info("[AdjustmentBalance] [Service] createAdjustmentBalanceLocalBackend Start");
        log.info("Input: apartmentID={}, feeID={}, adjustmentID={}, oldBalance={}, newBalance={}, adjustmentBalanceNote={}",
                apartmentID, feeID, adjustmentID, oldBalance, newBalance, adjustmentBalanceNote);

        try {
            AdjustmentBalance entity = new AdjustmentBalance();

            entity.setApartmentID(apartmentID);
            entity.setFeeID(feeID);
            entity.setAdjustmentID(adjustmentID);
            entity.setOldBalance(oldBalance);
            entity.setNewBalance(newBalance);
            entity.setAdjustmentBalanceNote(adjustmentBalanceNote);

            adjustmentBalanceRepository.create(entity);

            log.info("[AdjustmentBalance] [Service] createAdjustmentBalanceLocalBackend End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createAdjustmentBalanceLocalBackend Error", e);
            throw new InternalServerException(e.getMessage());
        }

    }

}
