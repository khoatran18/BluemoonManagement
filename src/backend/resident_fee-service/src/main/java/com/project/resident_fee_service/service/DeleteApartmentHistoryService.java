package com.project.resident_fee_service.service;

import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.DeleteApartmentHistoryDTO;
import com.project.resident_fee_service.entity.DeleteApartmentHistory;
import com.project.resident_fee_service.mapper.DeleteApartmentHistoryMapper;
import com.project.resident_fee_service.repository.DeleteApartmentHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DeleteApartmentHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteApartmentHistoryService.class);

    DeleteApartmentHistoryRepository repository =
            new DeleteApartmentHistoryRepository();

    DeleteApartmentHistoryMapper mapper =
            new DeleteApartmentHistoryMapper();

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    public DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseDTO
    getDeleteApartmentHistoriesByFilter(
            Long apartmentId,
            int page,
            int limit
    ) {

        log.info("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoriesByFilter Start");
        log.info("Input: apartmentId={}, page={}, limit={}", apartmentId, page, limit);

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<DeleteApartmentHistory> entityList =
                    repository.getByFilter(apartmentId, queryPage, queryLimit);

            long count =
                    repository.countByFilter(apartmentId);

            DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseDTO response =
                    new DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseDTO();

            response.Page = queryPage;
            response.Limit = queryLimit;
            response.TotalItems = count;
            response.DeleteApartmentHistories =
                    mapper.GetDeleteApartmentHistoriesResponseItemsEntityToDTO(entityList);

            log.info("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoriesByFilter End");
            log.info("Output: {}", response);

            return response;

        } catch (Exception e) {
            log.error("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoriesByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    public DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO
    getDeleteApartmentHistoryById(long historyId) {

        log.info("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoryById Start");
        log.info("Input: historyId={}", historyId);

        try {
            DeleteApartmentHistory entity = repository.findById(historyId);
            if (entity == null)
                throw new NotFoundException(
                        "DeleteApartmentHistory not found with id: " + historyId
                );

            DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO result =
                    mapper.GetDeleteApartmentHistoryResponseEntityToDTO(entity);

            log.info("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoryById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[DeleteApartmentHistory] [Service] getDeleteApartmentHistoryById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    /////////////////////////////
    // POST LOCAL BACKEND
    /////////////////////////////

    @Transactional
    public void createDeleteApartmentHistoryLocalBackend(
            Long apartmentId,
            String building,
            String roomNumber
    ) {

        log.info("[DeleteApartmentHistory] [Service] createDeleteApartmentHistoryLocalBackend Start");
        log.info(
                "Input: apartmentId={}, building={}, roomNumber={}",
                apartmentId, building, roomNumber
        );

        try {
            DeleteApartmentHistory entity = new DeleteApartmentHistory();

            entity.setApartmentId(apartmentId);
            entity.setBuilding(building);
            entity.setRoomNumber(roomNumber);
            entity.setDeletedAt(LocalDateTime.now());

            repository.create(entity);

            log.info("[DeleteApartmentHistory] [Service] createDeleteApartmentHistoryLocalBackend End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error(
                    "[DeleteApartmentHistory] [Service] createDeleteApartmentHistoryLocalBackend Error",
                    e
            );
            throw new InternalServerException(e.getMessage());
        }
    }

}
