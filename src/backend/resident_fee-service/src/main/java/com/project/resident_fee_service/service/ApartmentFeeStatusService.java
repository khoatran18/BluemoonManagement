package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.ApartmentFeeStatusDTO;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.mapper.ApartmentFeeStatusMapper;
import com.project.resident_fee_service.repository.ApartmentFeeStatusRepository;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApartmentFeeStatusService {

    @Inject
    ApartmentFeeStatusRepository repository;

    @Inject
    FeeRepository feeRepository;

    @Inject
    ApartmentRepository apartmentRepository;

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

            // 4. Update apartment paid in Fee
            Set<Fee> existingPaidFees = entity.getPaidFeeList();
            Set<ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef> incomingPaidFees = dto.paidFees;

            Set<Long> existingIds = existingPaidFees.stream()
                    .map(Fee::getFeeId)
                    .collect(Collectors.toSet());
            Set<Long> incomingIds = incomingPaidFees.stream()
                    .map(f -> f.feeId)
                    .collect(Collectors.toSet());
            Set<Long> newFeeIds = new HashSet<>(incomingIds);
            newFeeIds.removeAll(existingIds);

            Apartment apartment = entity.getApartment();
            for (Long feeId : existingIds) {
                Fee fee = feeRepository.findById(feeId);
                if (fee == null) {
                    throw new NotFoundException("Fee id not found: " + feeId); // hoặc throw nếu cần
                }

                // Add Apartment to paidList in Fee
                fee.getPaidApartmentList().add(apartment);
            }

        } catch (NotFoundException e) {
            throw e; // pass through
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
