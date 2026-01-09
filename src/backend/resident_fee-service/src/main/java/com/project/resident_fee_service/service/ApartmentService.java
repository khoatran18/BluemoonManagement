package com.project.resident_fee_service.service;

import com.project.common_package.exception.BadRequestException;
import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.dto.ApartmentDTO;
import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.resident_fee_service.dto.ResidentDTO;
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
import jakarta.persistence.EntityManager;
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
    EntityManager entityManager;

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

        log.info("[Apartment] [Service] getApartmentsByFilter Start");
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

            log.info("[Apartment] [Service] getApartmentsByFilter End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Apartment] [Service] getApartmentsByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // GET DETAIL
    ////////////////////////////////////////

    public ApartmentDetailsDTO getApartmentById(Long id) {

        log.info("[Apartment] [Service] getApartmentById Start");
        log.info("Input: apartmentId={}", id);

        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            ApartmentDetailsDTO result =
                    ApartmentDetailsMapper.toDTO(entity);

            log.info("[Apartment] [Service] getApartmentById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Apartment] [Service] getApartmentById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    public ApartmentSpecificAdjustmentsResponseDTO getApartmentSpecificAdjustments(Long id) {

        log.info("[Apartment] [Service] getApartmentSpecificAdjustments Start");
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

            log.info("[Apartment] [Service] getApartmentSpecificAdjustments End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Apartment] [Service] getApartmentSpecificAdjustments Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////

    @Transactional
    public void createApartment(ApartmentCreateDTO dto) {

        log.info("[Apartment] [Service] createApartment Start");
        log.info("Input: {}", dto);

        try {
            // Create apartment
            Apartment entity = ApartmentMutationMapper.toEntity(dto);
            apartmentRepository.persist(entity);

            // Init ApartmentFeeStatus
            ApartmentFeeStatus apartmentFeeStatus = new ApartmentFeeStatus();
            apartmentFeeStatus.setApartment(entity);
            apartmentFeeStatusRepository.persist(apartmentFeeStatus);

            // Attach residents to apartment
            Resident headResident = null;
            if (dto.headResidentId != null) {
                headResident = residentRepository.findById(dto.headResidentId);
                if (headResident == null)
                    throw new NotFoundException("Head resident not found: " + dto.headResidentId);
            }

            List<Resident> resolvedResidents = null;
            if (dto.residents != null && !dto.residents.isEmpty()) {

                if (dto.headResidentId == null) {
                    throw new BadRequestException(
                            "Head resident is required when residents list is provided"
                    );
                }

                boolean headInResidents = dto.residents.stream()
                        .anyMatch(r -> r.id.equals(dto.headResidentId));

                if (!headInResidents) {
                    throw new BadRequestException(
                            "Head resident must be included in residents list"
                    );
                }
            }

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

            ApartmentMutationMapper.createApartmentResident(
                    entity, dto, headResident, resolvedResidents
            );

            log.info("[Apartment] [Service] createApartment End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Apartment] [Service] createApartment Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////

    @Transactional
    public void updateApartment(Long id, ApartmentUpdateDTO dto) {

        log.info("[Apartment] [Service] updateApartment Start");
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
            if (dto.residents != null && !dto.residents.isEmpty()) {

                if (dto.headResidentId == null) {
                    throw new BadRequestException(
                            "Head resident is required when residents list is provided"
                    );
                }

                boolean headInResidents = dto.residents.stream()
                        .anyMatch(r -> r.id.equals(dto.headResidentId));

                if (!headInResidents) {
                    throw new BadRequestException(
                            "Head resident must be included in residents list"
                    );
                }
            }

            validateUpdateHeadRule(entity, dto.headResidentId, dto.residents);


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

            log.info("[Apartment] [Service] updateApartment End");
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

        log.info("[Apartment] [Service] updateSpecificAdjustments Start");
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

            log.info("[Apartment] [Service] updateSpecificAdjustments End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Apartment] [Service] updateSpecificAdjustments Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    ////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////

    @Transactional
    public void deleteApartment(Long apartmentId) {
        log.info("[Apartment] [Service] deleteApartment Start");
        log.info("Input: apartmentId={}", apartmentId);

        Apartment apartment = apartmentRepository.findById(apartmentId);
        if (apartment == null) {
            throw new NotFoundException("Apartment not found with id: " + apartmentId);
        }

        try {
            // 1. Unlink Residents
            if (apartment.getResidents() != null) {
                for (Resident resident : apartment.getResidents()) {
                    resident.setApartment(null);
                    resident.setIsHead(false); // They are no longer a head of this apartment
                }
                // Clear the list in the parent object to stop Hibernate from tracking them for deletion
                apartment.getResidents().clear();
            }

            // 2. Break Circular Dependency (Head Resident)
            apartment.setHeadResident(null);

            // 3. Clear Many-to-Many Adjustments
            apartment.getAdjustments().clear();

            // 4. Flush changes so the unlinking happens in DB before the delete
            apartmentRepository.flush();

            // 5. Delete in Apartment_PaidFee
            entityManager.createNativeQuery("""
                DELETE FROM Apartment_PaidFee Where ApartmentID = :id
            """).setParameter("id", apartmentId)
                    .executeUpdate();

            // 6. Delete Apartment
            apartmentRepository.delete(apartment);

            log.info("[Apartment] [Service] deleteApartment End");

        } catch (Exception e) {
            log.error("[Apartment] [Service] deleteApartment Error", e);

            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new com.project.common_package.exception.ConflictException(
                        "Cannot delete apartment due to existing data dependencies."
                );
            }
            throw new InternalServerException(e.getMessage());
        }
    }


    private void validateUpdateHeadRule(
            Apartment entity,
            Long newHeadId,
            List<ApartmentUpdateDTO.ResidentInfo> newResidents
    ) {
        if (newResidents == null) {
            return;
        }

        if (newResidents.isEmpty()) {
            return;
        }

        Resident oldHead = entity.getResidents().stream()
                .filter(Resident::getIsHead)
                .findFirst()
                .orElse(null);

        boolean oldHeadStillExists = oldHead != null
                && newResidents.stream()
                .anyMatch(r -> r.id.equals(oldHead.getResidentId()));

        if (!oldHeadStillExists) {

            if (newHeadId == null) {
                throw new BadRequestException(
                        "Previous head is removed. A new head must be specified or residents list must be empty"
                );
            }

            boolean newHeadInList = newResidents.stream()
                    .anyMatch(r -> r.id.equals(newHeadId));

            if (!newHeadInList) {
                throw new BadRequestException(
                        "New head resident must be included in residents list"
                );
            }
        }
    }

}
