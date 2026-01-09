package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.DeleteResidentHistoryDTO;
import com.project.resident_fee_service.entity.DeleteResidentHistory;
import com.project.resident_fee_service.mapper.DeleteResidentHistoryMapper;
import com.project.resident_fee_service.repository.DeleteResidentHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DeleteResidentHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteResidentHistoryService.class);

    DeleteResidentHistoryRepository repository =
            new DeleteResidentHistoryRepository();

    DeleteResidentHistoryMapper mapper =
            new DeleteResidentHistoryMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseDTO
    getDeleteResidentHistoriesByFilter(
            Long residentId,
            Long apartmentId,
            int page,
            int limit
    ) {

        log.info("[DeleteResidentHistory] [Service] getDeleteResidentHistoriesByFilter Start");
        log.info(
                "Input: residentId={}, apartmentId={}, page={}, limit={}",
                residentId, apartmentId, page, limit
        );

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<DeleteResidentHistory> entityList =
                    repository.getByFilter(residentId, apartmentId, queryPage, queryLimit);

            long count =
                    repository.countByFilter(residentId, apartmentId);

            DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseDTO response =
                    new DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseDTO();

            response.Page = queryPage;
            response.Limit = queryLimit;
            response.TotalItems = count;
            response.DeleteResidentHistories =
                    mapper.GetDeleteResidentHistoriesResponseItemsEntityToDTO(entityList);

            log.info("[DeleteResidentHistory] [Service] getDeleteResidentHistoriesByFilter End");
            log.info("Output: {}", response);

            return response;

        } catch (Exception e) {
            log.error("[DeleteResidentHistory] [Service] getDeleteResidentHistoriesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO
    getDeleteResidentHistoryById(long historyId) {

        log.info("[DeleteResidentHistory] [Service] getDeleteResidentHistoryById Start");
        log.info("Input: historyId={}", historyId);

        try {
            DeleteResidentHistory entity = repository.findById(historyId);
            if (entity == null)
                throw new NotFoundException(
                        "DeleteResidentHistory not found with id: " + historyId
                );

            DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO result =
                    mapper.GetDeleteResidentHistoryResponseEntityToDTO(entity);

            log.info("[DeleteResidentHistory] [Service] getDeleteResidentHistoryById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[DeleteResidentHistory] [Service] getDeleteResidentHistoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    /////////////////////////////
    // POST LOCAL BACKEND
    /////////////////////////////

    @Transactional
    public void createDeleteResidentHistoryLocalBackend(
            Long residentId,
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email,
            Boolean isHead
    ) {

        log.info("[DeleteResidentHistory] [Service] createDeleteResidentHistoryLocalBackend Start");
        log.info(
                "Input: residentId={}, apartmentId={}, fullName={}, phoneNumber={}, email={}, isHead={}",
                residentId, apartmentId, fullName, phoneNumber, email, isHead
        );

        try {
            DeleteResidentHistory entity = new DeleteResidentHistory();

            entity.setResidentId(residentId);
            entity.setApartmentId(apartmentId);
            entity.setFullName(fullName);
            entity.setPhoneNumber(phoneNumber);
            entity.setEmail(email);
            entity.setIsHead(isHead);
            entity.setDeletedAt(LocalDateTime.now());

            repository.create(entity);

            log.info("[DeleteResidentHistory] [Service] createDeleteResidentHistoryLocalBackend End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error(
                    "[DeleteResidentHistory] [Service] createDeleteResidentHistoryLocalBackend Error",
                    e
            );
            throw new InternalServerException(e.getMessage());
        }
    }

}
