package com.project.service;

import com.project.dto.*;
import com.project.entity.Resident;
import com.project.entity.Apartment;
import com.project.exception.BadRequestException;
import com.project.exception.BusinessException;
import com.project.mapper.*;
import com.project.repository.ResidentRepository;
import com.project.repository.ApartmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ResidentService {

    @Inject
    ResidentRepository residentRepository;

    @Inject
    ApartmentRepository apartmentRepository;

    /**
     * GET /api/v1/residents/{id}
     * Uses fetch-join to avoid N+1
     */
    public ResidentDetailsDTO getDetails(Long residentId) {
        try {
            Resident entity = residentRepository.findWithApartment(residentId);
            if (entity == null) {
                throw new BadRequestException("Resident not found");
            }
            return ResidentDetailsMapper.toDTO(entity);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to load resident details");
        }
    }

    /**
     * GET /api/v1/residents (search + paging)
     */
    public ResidentListResponseDTO list(
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email,
            int page,
            int limit
    ) {
        try {
            List<Resident> residents =
                    residentRepository.search(apartmentId, fullName, phoneNumber, email, page, limit);

            long total =
                    residentRepository.countSearch(apartmentId, fullName, phoneNumber, email);

            return ResidentListResponseMapper.toDTO(page, limit, (int) total, residents);

        } catch (Exception e) {
            throw new BusinessException("Failed to list residents");
        }
    }

    /**
     * POST /api/v1/residents
     * Behavior (API-accurate):
     * - apartmentId is passed separately by controller, required
     * - owner-id & isHead are NOT part of API and therefore not handled
     */
    @Transactional
    public ResidentDetailsDTO create(ResidentCreateDTO dto, Long apartmentId) {
        try {
            if (dto == null) {
                throw new BadRequestException("Request body is required");
            }

            if (apartmentId == null) {
                throw new BadRequestException("apartment_id is required");
            }

            Apartment apartment = apartmentRepository.findById(apartmentId);
            if (apartment == null) {
                throw new BadRequestException("Apartment not found");
            }

            if (dto.fullName == null || dto.fullName.isBlank()) {
                throw new BadRequestException("full_name is required");
            }

            Resident entity = ResidentMutationMapper.toEntity(dto, apartment);

            // isHead is not allowed to be set here; always default -> false
            entity.setIsHead(false);

            residentRepository.persist(entity);

            return ResidentDetailsMapper.toDTO(entity);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to create resident");
        }
    }

    /**
     * PUT /api/v1/residents/{id}
     * Behavior:
     * - Updates only primitive fields (full_name, phone_number, email)
     * - Disallows apartment reassignment via this endpoint
     * - Does NOT touch head-resident logic (handled only by ApartmentService)
     */
    @Transactional
    public ResidentDetailsDTO update(Long id, ResidentUpdateDTO dto, Long apartmentId) {
        try {
            if (dto == null) {
                throw new BadRequestException("Request body is required");
            }

            Resident entity = residentRepository.findById(id);
            if (entity == null) {
                throw new BadRequestException("Resident not found");
            }

            // If apartmentId provided, must match existing apartment
            if (apartmentId != null &&
                    !Objects.equals(apartmentId, entity.getApartment().getApartmentId())) {

                throw new BadRequestException(
                        "Changing apartment through resident update is not allowed. Use apartment endpoints.");
            }

            // Validation
            if (dto.fullName != null && dto.fullName.isBlank()) {
                throw new BadRequestException("full_name cannot be blank");
            }

            // Update only primitive fields (mapper must NOT update apartment / owner / isHead)
            ResidentMutationMapper.updateEntity(entity, dto);

            return ResidentDetailsMapper.toDTO(entity);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to update resident");
        }
    }

    /**
     * DELETE /api/v1/residents/{id}
     */
    @Transactional
    public void delete(Long id) {
        try {
            boolean removed = residentRepository.deleteById(id);
            if (!removed) {
                throw new BadRequestException("Resident not found");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to delete resident");
        }
    }
}
