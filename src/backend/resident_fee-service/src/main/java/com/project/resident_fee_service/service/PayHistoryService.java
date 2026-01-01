package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.PayHistoryDTO;
import com.project.resident_fee_service.entity.PayHistory;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.mapper.PayHistoryMapper;
import com.project.resident_fee_service.repository.FeeCategoryRepository;
import com.project.resident_fee_service.repository.FeeRepository;
import com.project.resident_fee_service.repository.FeeTypeRepository;
import com.project.resident_fee_service.repository.PayHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PayHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(PayHistoryService.class);

    PayHistoryRepository payHistoryRepository = new PayHistoryRepository();


    PayHistoryMapper payHistoryMapper = new PayHistoryMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////


    public PayHistoryDTO.GetPayHistoriesResponseDTO getPayHistoriesByFilter(
            Long apartmentID,
            Long feeID,
            int page,
            int limit
    ) {

        log.info("[PayHistory] [Service] getPayHistoriesByFilter Start");
        log.info(
                "Input: apartmentID={}, feeID={}, page={}, limit={}",
                apartmentID, feeID, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<PayHistory> feeEntityList =
                    payHistoryRepository.getByFilter(
                            apartmentID, feeID, queryPage, queryLimit
                    );

            long count =
                    payHistoryRepository.countByFilter(
                            apartmentID, feeID
                    );

            List<PayHistoryDTO.GetPayHistoriesResponseItemDTO> payHistoriesDTO =
                    payHistoryMapper.GetPayHistoriesResponseItemsEntityToDTO(feeEntityList);

            PayHistoryDTO.GetPayHistoriesResponseDTO responseData =
                    new PayHistoryDTO.GetPayHistoriesResponseDTO();

            responseData.Page = queryPage;
            responseData.Limit = queryLimit;
            responseData.TotalItems = count;
            responseData.PayHistories = payHistoriesDTO;

            log.info("[PayHistory] [Service] getPayHistoriesByFilter End");
            log.info("Output: {}", responseData);

            return responseData;

        } catch (Exception e) {
            log.error("[PayHistory] [Service] getPayHistoriesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }

    }


    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public PayHistoryDTO.GetPayHistoryResponseDTO getPayHistoryById(long payHistoryID) {

        log.info("[PayHistory] [Service] getPayHistoryById Start");
        log.info("Input: payHistoryID={}", payHistoryID);

        try {
            PayHistory payHistory = payHistoryRepository.findById(payHistoryID);
            if (payHistory == null)
                throw new NotFoundException("PayHistory not found with id: " + payHistoryID);



            PayHistoryDTO.GetPayHistoryResponseDTO result =
                    payHistoryMapper.GetPayHistoryResponseEntityToDTO(payHistory);

            log.info("[PayHistory] [Service] getPayHistoryById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[PayHistory] [Service] getPayHistoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    /////////////////////////////
    // POST LOCAL BACKEND
    /////////////////////////////

    @Transactional
    public void createPayHistoryLocalBackend(
            Long apartmentID,
            Long feeID,
            String payDatetime,
            BigDecimal payAmount,
            String payNote
    ) {

        log.info("[PayHistory] [Service] createPayHistoryLocalBackend Start");
        log.info("Input: apartmentID={}, feeID={}, payDatetime={}, payAmount={}, payNote={}",
                apartmentID, feeID, payDatetime, payAmount, payNote);

        try {
            PayHistory entity = new PayHistory();

            entity.setApartmentID(apartmentID);
            entity.setFeeID(feeID);
            entity.setPayDateTime(LocalDateMapper.StringToLocalDate(payDatetime));
            entity.setPayAmount(payAmount);
            entity.setPayNote(payNote);

            payHistoryRepository.create(entity);

            log.info("[PayHistory] [Service] createPayHistoryLocalBackend End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Fee] [Service] createPayHistoryLocalBackend Error", e);
            throw new InternalServerException(e.getMessage());
        }

    }
}
