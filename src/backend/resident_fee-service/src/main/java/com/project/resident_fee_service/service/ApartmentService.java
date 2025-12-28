package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.resident_fee_service.entity.Resident;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.mapper.AdjustmentMapper;
import com.project.resident_fee_service.mapper.ApartmentDetailsMapper;
import com.project.resident_fee_service.mapper.ApartmentListResponseMapper;
import com.project.resident_fee_service.mapper.ApartmentMutationMapper;
import com.project.resident_fee_service.repository.AdjustmentRepository;
import com.project.resident_fee_service.repository.ApartmentFeeStatusRepository;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.ResidentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ApartmentService {

    private static final Logger log =
            LoggerFactory.getLogger(ApartmentService.class);

    @Inject
    ApartmentRepository apartmentRepository;

    @Inject
    AdjustmentRepository adjustmentRepository;

    @Inject
    ResidentRepository residentRepository;

    @Inject
    ApartmentFeeStatusRepository apartmentFeeStatusRepository;

    AdjustmentMapper adjustmentMapper = new AdjustmentMapper();

    ////////////////////////////////////////
    // GET LIST
    ////////////////////////////////////////

    public ApartmentListResponseDTO getApartmentsByFilter(
            String building,
            String roomNumber,
            Long headResidentId,
            int page,
            int limit
    ) {

        log.info("[Resident] [Service] getApartmentsByFilter Start");
        log.info("Input: building={}, roomNumber={}, headResidentId={}, page={}, limit={}",
                building, roomNumber, headResidentId, page, limit);

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<Apartment> entityList =
                    apartmentRepository.getByFilter(
                            building, roomNumber, headResidentId,
                            queryPage, queryLimit
                    );

            long count =
                    apartmentRepository.countByFilter(
                            building, roomNumber, headResidentId
                    );

            ApartmentListResponseDTO result =
                    ApartmentListResponseMapper.toDTO(
                            queryPage, queryLimit, count, entityList
                    );

            log.info("[Resident] [Service] getApartmentsByFilter End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Resident] [Service] getApartmentsByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // GET DETAIL
    ////////////////////////////////////////

    public ApartmentDetailsDTO getApartmentById(Long id) {

        log.info("[Resident] [Service] getApartmentById Start");
        log.info("Input: apartmentId={}", id);

        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            ApartmentDetailsDTO result =
                    ApartmentDetailsMapper.toDTO(entity);

            log.info("[Resident] [Service] getApartmentById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Resident] [Service] getApartmentById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    public ApartmentSpecificAdjustmentsResponseDTO getApartmentSpecificAdjustments(Long id) {

        log.info("[Resident] [Service] getApartmentSpecificAdjustments Start");
        log.info("Input: apartmentId={}", id);

        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            List<Adjustment> adjustmentsEntity = entity.getAdjustments();
            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentDTOs =
                    adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentsEntity);

            ApartmentSpecificAdjustmentsResponseDTO result =
                    new ApartmentSpecificAdjustmentsResponseDTO();
            result.adjustments = adjustmentDTOs;

            log.info("[Resident] [Service] getApartmentSpecificAdjustments End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Resident] [Service] getApartmentSpecificAdjustments Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////

    @Transactional
    public void createApartment(ApartmentCreateDTO dto) {

        log.info("[Resident] [Service] createApartment Start");
        log.info("Input: {}", dto);

        try {
            Apartment entity = ApartmentMutationMapper.toEntity(dto);
            apartmentRepository.persist(entity);

            ApartmentFeeStatus apartmentFeeStatus = new ApartmentFeeStatus();
            apartmentFeeStatus.setApartment(entity);
            apartmentFeeStatusRepository.persist(apartmentFeeStatus);

            log.info("[Resident] [Service] createApartment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] createApartment Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////

    @Transactional
    public void updateApartment(Long id, ApartmentUpdateDTO dto) {

        log.info("[Resident] [Service] updateApartment Start");
        log.info("Input: apartmentId={}, dto={}", id, dto);

        try {
            Apartment entity = apartmentRepository.findById(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            // Set null apartment to old resident
            List<Resident> oldResidents = entity.getResidents();
            for (Resident r : oldResidents) {
                r.setApartment(null);
                r.setIsHead(false);
            }

            // Attach new residents to apartment
            Resident headResident = null;
            if (dto.headResidentId != null) {
                headResident = residentRepository.findById(dto.headResidentId);
                if (headResident == null)
                    throw new NotFoundException("Head resident not found: " + dto.headResidentId);
            }

            List<Resident> resolvedResidents = null;
            if (dto.residents != null) {
                resolvedResidents = dto.residents.stream()
                        .map(r -> {
                            Resident found = residentRepository.findById(r.id);
                            if (found == null)
                                throw new NotFoundException("Resident not found with id: " + r.id);
                            found.setIsHead(false);
                            return found;
                        })
                        .toList();
            }
            if (headResident != null)
                headResident.setIsHead(true);

            ApartmentMutationMapper.updateEntity(
                    entity, dto, headResident, resolvedResidents
            );

            log.info("[Resident] [Service] updateApartment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] updateApartment Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    @Transactional
    public void updateSpecificAdjustments(
            Long apartmentId,
            ApartmentSpecificAdjustmentsRequestDTO adjustmentIds
    ) {

        log.info("[Resident] [Service] updateSpecificAdjustments Start");
        log.info("Input: apartmentId={}, dto={}", apartmentId, adjustmentIds);

        try {
            Apartment apartment = apartmentRepository.findById(apartmentId);

            List<Adjustment> adjustments = new ArrayList<>();
            for (Long adjustmentId : adjustmentIds.adjustmentIds) {
                Adjustment adjustment = adjustmentRepository.findById(adjustmentId);
                if (adjustment == null)
                    throw new NotFoundException("Adjustment not found with id: " + adjustmentId);
                adjustments.add(adjustment);
            }

            apartment.setAdjustments(adjustments);

            log.info("[Resident] [Service] updateSpecificAdjustments End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] updateSpecificAdjustments Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////

    @Transactional
    public void deleteApartment(Long apartmentId) {

        log.info("[Resident] [Service] deleteApartment Start");
        log.info("Input: apartmentId={}", apartmentId);

        try {
            Apartment entity = apartmentRepository.findById(apartmentId);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + apartmentId);

            apartmentRepository.delete(entity);

            log.info("[Resident] [Service] deleteApartment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] deleteApartment Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
