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

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ApartmentService {

    @Inject
    ApartmentRepository apartmentRepository;

    @Inject
    AdjustmentRepository adjustmentRepository;

    @Inject
    ResidentRepository residentRepository;

    @Inject
    ApartmentFeeStatusRepository apartmentFeeStatusRepository;

    AdjustmentMapper adjustmentMapper = new AdjustmentMapper();


    ///////////////////////////////// GET LIST /////////////////////////////////

    /**
     * GET /api/v1/apartments
     */
    public ApartmentListResponseDTO getApartmentsByFilter(
            String building,
            String roomNumber,
            Long headResidentId,
            int page,
            int limit
    ) {

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            // Retrieve DB
            List<Apartment> entityList = apartmentRepository.getByFilter(
                    building,
                    roomNumber,
                    headResidentId,
                    queryPage,
                    queryLimit
            );

            long count = apartmentRepository.countByFilter(
                    building,
                    roomNumber,
                    headResidentId
            );
            return ApartmentListResponseMapper.toDTO(queryPage, queryLimit, count, entityList);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


    ///////////////////////////////// GET DETAIL /////////////////////////////////

    /**
     * GET /api/v1/apartments/{id}
     */
    public ApartmentDetailsDTO getApartmentById(Long id) {
        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            return ApartmentDetailsMapper.toDTO(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * GET /api/v1/apartments/apartment_specific_adjustments/{apartment_id}
     */
    public ApartmentSpecificAdjustmentsResponseDTO getApartmentSpecificAdjustments(Long id) {
        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + id);

            List<Adjustment> adjustmentsEntity = entity.getAdjustments();
            List<AdjustmentDTO.GetAdjustmentsResponseItemDTO> adjustmentDTOs = adjustmentMapper.GetAdjustmentsResponseItemsEntityToDTO(adjustmentsEntity);

            ApartmentSpecificAdjustmentsResponseDTO result = new ApartmentSpecificAdjustmentsResponseDTO();
            result.adjustments = adjustmentDTOs;

            return result;

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    ///////////////////////////////// CREATE /////////////////////////////////

    /**
     * POST /api/v1/apartments
     */
    @Transactional
    public void createApartment(ApartmentCreateDTO dto) {
        try {
            Apartment entity = ApartmentMutationMapper.toEntity(dto);
            apartmentRepository.persist(entity);

            Long apartmentId = entity.getApartmentId();
            ApartmentFeeStatus apartmentFeeStatus = new ApartmentFeeStatus();
            apartmentFeeStatus.setApartment(entity);

            apartmentFeeStatusRepository.persist(apartmentFeeStatus);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


    ///////////////////////////////// UPDATE /////////////////////////////////

    /**
     * PUT /api/v1/apartments/{id}
     */
    @Transactional
    public void updateApartment(Long id, ApartmentUpdateDTO dto) {
        try {
            Apartment entity = apartmentRepository.findById(id);
            if (entity == null) {
                throw new NotFoundException("Apartment not found with id: " + id);
            }

            // Resolve head resident (null if clearing head)
            Resident headResident = null;
            if (dto.headResidentId != null) {
                headResident = residentRepository.findById(dto.headResidentId);
                if (headResident == null) {
                    throw new NotFoundException("Head resident not found: " + dto.headResidentId);
                }
            }

            // Resolve residents list ONLY IF dto.residents != null
            List<Resident> resolvedResidents = null;
            if (dto.residents != null) {
                resolvedResidents = dto.residents.stream()
                        .map(r -> {
                            Resident found = residentRepository.findById(r.id);
                            if (found == null) {
                                throw new NotFoundException("Resident not found with id: " + r.id);
                            }
                            return found;
                        })
                        .toList();
            }

            // IMPORTANT: pass exactly what mapper expects
            ApartmentMutationMapper.updateEntity(
                    entity,
                    dto,
                    headResident,
                    resolvedResidents
            );

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /**
     * PUT /api/v1/apartments/apartment_specific_adjustments/{apartment_id}
     */
    @Transactional
    public void updateSpecificAdjustments(Long apartmentId, ApartmentSpecificAdjustmentsRequestDTO adjustmentIds) {

        Apartment apartment = apartmentRepository.findById(apartmentId);

        List<Adjustment> adjustments = new ArrayList<>();
        for (Long adjustmentId : adjustmentIds.adjustmentIds) {
            Adjustment adjustment = adjustmentRepository.findById(adjustmentId);
            if (adjustment == null) {
                throw new NotFoundException("Adjustment not found with id: " + adjustmentId);
            }
            adjustments.add(adjustment);
        }

        apartment.setAdjustments(adjustments);
    }


    ///////////////////////////////// DELETE /////////////////////////////////

    /**
     * DELETE /api/v1/apartments/{id}
     */
    @Transactional
    public void deleteApartment(Long apartmentId) {
        try {
            Apartment entity = apartmentRepository.findById(apartmentId);
            if (entity == null)
                throw new NotFoundException("Apartment not found with id: " + apartmentId);

            apartmentRepository.delete(entity);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
