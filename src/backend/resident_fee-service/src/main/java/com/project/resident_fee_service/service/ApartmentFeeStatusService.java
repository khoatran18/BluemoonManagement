package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.ApartmentFeeStatusDTO;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.mapper.ApartmentFeeStatusMapper;
import com.project.resident_fee_service.repository.ApartmentFeeStatusRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ApartmentFeeStatusService {

    @Inject
    ApartmentFeeStatusRepository repository;

    ApartmentFeeStatusMapper mapper = new ApartmentFeeStatusMapper();

    // ==========================================================
    // =============== 5.1 GET Method ===========================
    // ==========================================================

    /**
     * Handle logic for GET /apartment-fee-statuses/{apartment_id}:
     * - Verify the status exists
     * - Convert entity → response DTO
     */
    public ApartmentFeeStatusDTO.FeeStatusDTO getStatusByApartmentId(Long apartmentId) {

        try {
            // 1. Retrieve entity
            ApartmentFeeStatus entity = repository.findByApartmentId(apartmentId);
            if (entity == null)
                throw new NotFoundException("Apartment fee status not found for apartment id: " + apartmentId);

            // 2. Convert to DTO
            return mapper.entityToDTO(entity);

        } catch (NotFoundException e) {
            throw e; // pass through
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


    // ==========================================================
    // =============== 5.2 PUT Method ===========================
    // ==========================================================

    /**
     * Handle logic for PUT /apartment-fee-statuses/{apartment_id}:
     * - Retrieve status entity
     * - Apply updates using DTO (mapper mutates entity)
     * - Persist changes
     */
    @Transactional
    public void updateStatusByApartmentId(
            Long apartmentId,
            ApartmentFeeStatusDTO.FeeStatusUpdateDTO dto
    ) {

        try {
            // 1. Retrieve entity
            ApartmentFeeStatus entity = repository.findByApartmentId(apartmentId);
            if (entity == null)
                throw new NotFoundException("Apartment fee status not found for apartment id: " + apartmentId);

            // 2. Apply updates to the entity
            mapper.applyUpdateDTOToEntity(entity, dto);

            // 3. Persist changes (Panache tracks entity automatically)
            repository.updateStatus(entity);

        } catch (NotFoundException e) {
            throw e; // pass through
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
