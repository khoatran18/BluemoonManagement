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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApartmentFeeStatusService {

    private static final Logger log =
            LoggerFactory.getLogger(ApartmentFeeStatusService.class);

    @Inject
    ApartmentFeeStatusRepository repository;

    @Inject
    FeeRepository feeRepository;

    @Inject
    ApartmentRepository apartmentRepository;

    ApartmentFeeStatusMapper mapper = new ApartmentFeeStatusMapper();

    // ==========================================================
    // GET
    // ==========================================================

    public ApartmentFeeStatusDTO.FeeStatusDTO getStatusByApartmentId(
            Long apartmentId
    ) {

        log.info("[Fee] [Service] getStatusByApartmentId Start");
        log.info("Input: apartmentId={}", apartmentId);

        try {
            ApartmentFeeStatus entity =
                    repository.findByApartmentId(apartmentId);
            if (entity == null)
                throw new NotFoundException(
                        "Apartment fee status not found for apartment id: " + apartmentId
                );

            ApartmentFeeStatusDTO.FeeStatusDTO result =
                    mapper.entityToDTO(entity);

            log.info("[Fee] [Service] getStatusByApartmentId End");
            log.info("Output: {}", result);

            return result;

        } catch (NotFoundException e) {
            log.error("[Fee] [Service] getStatusByApartmentId Error", e);
            throw e;
        } catch (Exception e) {
            log.error("[Fee] [Service] getStatusByApartmentId Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // ==========================================================
    // PUT
    // ==========================================================

    @Transactional
    public void updateStatusByApartmentId(
            Long apartmentId,
            ApartmentFeeStatusDTO.FeeStatusUpdateDTO dto
    ) {

        log.info("[Fee] [Service] updateStatusByApartmentId Start");
        log.info("Input: apartmentId={}, dto={}", apartmentId, dto);

        try {
            ApartmentFeeStatus entity =
                    repository.findByApartmentId(apartmentId);
            if (entity == null)
                throw new NotFoundException(
                        "Apartment fee status not found for apartment id: " + apartmentId
                );

            mapper.applyUpdateDTOToEntity(entity, dto);
            repository.updateStatus(entity);

            Set<Fee> existingPaidFees = entity.getPaidFeeList();
            Set<ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef> incomingPaidFees =
                    dto.paidFees;

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
                if (fee == null)
                    throw new NotFoundException("Fee id not found: " + feeId);

                fee.getPaidApartmentList().add(apartment);
            }

            log.info("[Fee] [Service] updateStatusByApartmentId End");
            log.info("Output: None");

        } catch (NotFoundException e) {
            log.error("[Fee] [Service] updateStatusByApartmentId Error", e);
            throw e;
        } catch (Exception e) {
            log.error("[Fee] [Service] updateStatusByApartmentId Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
