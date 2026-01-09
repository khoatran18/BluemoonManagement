package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.DeleteFeeHistoryDTO;
import com.project.resident_fee_service.entity.DeleteFeeHistory;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.mapper.DeleteFeeHistoryMapper;
import com.project.resident_fee_service.repository.DeleteFeeHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DeleteFeeHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteFeeHistoryService.class);

    DeleteFeeHistoryRepository repository =
            new DeleteFeeHistoryRepository();

    DeleteFeeHistoryMapper mapper =
            new DeleteFeeHistoryMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseDTO
    getDeleteFeeHistoriesByFilter(
            Long feeId,
            Long feeTypeId,
            int page,
            int limit
    ) {

        log.info("[DeleteFeeHistory] [Service] getDeleteFeeHistoriesByFilter Start");
        log.info(
                "Input: feeId={}, feeTypeId={}, page={}, limit={}",
                feeId, feeTypeId, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<DeleteFeeHistory> entityList =
                    repository.getByFilter(feeId, feeTypeId, queryPage, queryLimit);

            long count =
                    repository.countByFilter(feeId, feeTypeId);

            DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseDTO response =
                    new DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseDTO();

            response.Page = queryPage;
            response.Limit = queryLimit;
            response.TotalItems = count;
            response.DeleteFeeHistories =
                    mapper.GetDeleteFeeHistoriesResponseItemsEntityToDTO(entityList);

            log.info("[DeleteFeeHistory] [Service] getDeleteFeeHistoriesByFilter End");
            log.info("Output: {}", response);

            return response;

        } catch (Exception e) {
            log.error("[DeleteFeeHistory] [Service] getDeleteFeeHistoriesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO
    getDeleteFeeHistoryById(long historyId) {

        log.info("[DeleteFeeHistory] [Service] getDeleteFeeHistoryById Start");
        log.info("Input: historyId={}", historyId);

        try {
            DeleteFeeHistory entity = repository.findById(historyId);
            if (entity == null)
                throw new NotFoundException(
                        "DeleteFeeHistory not found with id: " + historyId
                );

            DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO result =
                    mapper.GetDeleteFeeHistoryResponseEntityToDTO(entity);

            log.info("[DeleteFeeHistory] [Service] getDeleteFeeHistoryById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[DeleteFeeHistory] [Service] getDeleteFeeHistoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    /////////////////////////////
    // POST LOCAL BACKEND
    /////////////////////////////

    @Transactional
    public void createDeleteFeeHistoryLocalBackend(
            Long feeId,
            Long feeTypeId,
            Long feeCategoryId,
            String feeName,
            String feeDescription,
            String applicableMonth,
            BigDecimal amount,
            LocalDate startDate,
            LocalDate endDate,
            Fee.FeeStatus status
    ) {

        log.info("[DeleteFeeHistory] [Service] createDeleteFeeHistoryLocalBackend Start");
        log.info(
                "Input: feeId={}, feeTypeId={}, feeCategoryId={}, feeName={}, status={}",
                feeId, feeTypeId, feeCategoryId, feeName, status
        );

        try {
            DeleteFeeHistory entity = new DeleteFeeHistory();

            entity.setFeeId(feeId);
            entity.setFeeTypeId(feeTypeId);
            entity.setFeeCategoryId(feeCategoryId);
            entity.setFeeName(feeName);
            entity.setFeeDescription(feeDescription);
            entity.setApplicableMonth(applicableMonth);
            entity.setAmount(amount);
            entity.setStartDate(startDate);
            entity.setEndDate(endDate);
            entity.setStatus(status);
            entity.setDeletedAt(LocalDateTime.now());

            repository.create(entity);

            log.info("[DeleteFeeHistory] [Service] createDeleteFeeHistoryLocalBackend End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error(
                    "[DeleteFeeHistory] [Service] createDeleteFeeHistoryLocalBackend Error",
                    e
            );
            throw new InternalServerException(e.getMessage());
        }
    }

}
