package com.project.service;

import com.project.dto.*;
import com.project.entity.Apartment;
import com.project.entity.Resident;
import com.project.exception.BadRequestException;
import com.project.exception.BusinessException;
import com.project.mapper.*;
import com.project.repository.ApartmentRepository;
import com.project.repository.ResidentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApartmentService {

    @Inject
    ApartmentRepository apartmentRepository;

    @Inject
    ResidentRepository residentRepository;

    /**
     * GET /api/v1/apartments/{id}
     * Uses findWithResidents to avoid N+1
     */
    public ApartmentDetailsDTO getDetails(Long apartmentId) {
        try {
            Apartment entity = apartmentRepository.findWithResidents(apartmentId);
            if (entity == null) {
                throw new BadRequestException("Apartment not found");
            }
            return ApartmentDetailsMapper.toDTO(entity);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to load apartment details");
        }
    }

    /**
     * GET /api/v1/apartments (search + pagination)
     * Returns the DTO with page/limit/total_items/items
     */
    public ApartmentListResponseDTO list(
            String building,
            String roomNumber,
            Long headResidentId,
            int page,
            int limit
    ) {
        try {
            // repo returns paged results
            List<Apartment> apartments = apartmentRepository.search(building, roomNumber, headResidentId, page, limit);
            long total = apartmentRepository.countSearch(building, roomNumber, headResidentId);
            return ApartmentListResponseMapper.toDTO(page, limit, (int) total, apartments);
        } catch (Exception e) {
            throw new BusinessException("Failed to list apartments");
        }
    }

    /**
     * POST /api/v1/apartments
     */
    @Transactional
    public ApartmentDetailsDTO create(ApartmentCreateDTO dto) {
        try {
            Apartment entity = ApartmentMutationMapper.toEntity(dto);
            apartmentRepository.persist(entity);
            // ensure persisted entity is returned with residents empty
            return ApartmentDetailsMapper.toDTO(entity);
        } catch (Exception e) {
            throw new BusinessException("Failed to create apartment");
        }
    }

    /**
     * PUT /api/v1/apartments/{id}
     *
     * Behavior:
     * - Update building/room_number
     * - Replace resident list with dto.residents (IDs)
     * - For each resident in dto.residents: ensure exists, set apartment FK to this apartment
     * - Remove residents that are not in incoming list
     * - Validate and set head resident (if provided) — head must be in the final residents list
     * - Ensure isHead flags are consistent: exactly the head resident (if any) should have isHead = true, others false
     */
    @Transactional
    public ApartmentDetailsDTO update(Long id, ApartmentUpdateDTO dto) {
        try {
            Apartment entity = apartmentRepository.findWithResidents(id);
            if (entity == null) {
                throw new BadRequestException("Apartment not found");
            }

            // 1) Update simple fields + head (head set later after list)
            // Leave head handling to after resident reassignment
            if (dto.building == null || dto.building.isBlank())
                throw new BadRequestException("Building is required");

            entity.setBuilding(dto.building);
            if (dto.roomNumber == null || dto.roomNumber.isBlank())
                throw new BadRequestException("Room number is required");

            entity.setRoomNumber(dto.roomNumber);

            // 2) Build new resident list from DTO
            List<Long> newResidentIds = dto.residents == null ? Collections.emptyList()
                    : dto.residents.stream().map(r -> r.id).filter(Objects::nonNull).collect(Collectors.toList());

            // 3) Load all new resident entities in DB (and validate existence)
            List<Resident> newResidents = new ArrayList<>();
            for (Long rid : newResidentIds) {
                Resident r = residentRepository.findById(rid);
                if (r == null) {
                    throw new BadRequestException("Resident not found: " + rid);
                }
                newResidents.add(r);
            }

            // 4) Remove residents not in new list:
            // Use iterator to avoid ConcurrentModification
            Iterator<Resident> it = entity.getResidents().iterator();
            while (it.hasNext()) {
                Resident current = it.next();
                if (!newResidentIds.contains(current.getResidentId())) {
                    // detach: orphanRemoval = true on Apartment.residents -> JPA will delete or update as configured
                    current.setApartment(null);
                    it.remove();
                }
            }

            // 5) Add or ensure membership for each new resident; update their apartment FK
            for (Resident r : newResidents) {
                // if resident belongs to another apartment, reassigning is allowed by API
                r.setApartment(entity);
                if (!entity.getResidents().contains(r)) {
                    entity.getResidents().add(r);
                }
            }

            // 6) Handle head resident:
            // 6) Handle head resident:
            if (dto.headResidentId != null) {

                // Special case: explicitly clear head
                if (dto.headResidentId == 0) {
                    entity.setHeadResident(null);
                } else {
                    Resident head = residentRepository.findById(dto.headResidentId);
                    if (head == null) {
                        throw new BadRequestException("Head resident not found");
                    }

                    boolean inList = entity.getResidents().stream()
                            .anyMatch(r -> Objects.equals(r.getResidentId(), head.getResidentId()));

                    if (!inList) {
                        throw new BadRequestException("Head resident must be part of the apartment residents");
                    }

                    entity.setHeadResident(head);
                }

            } else {
                // Head not provided => leave existing unless it's no longer a resident
                Resident existingHead = entity.getHeadResident();
                boolean stillInList = existingHead != null &&
                        entity.getResidents().stream()
                                .anyMatch(r -> Objects.equals(r.getResidentId(), existingHead.getResidentId()));

                if (!stillInList) {
                    entity.setHeadResident(null);
                }
            }



            // 7) Maintain isHead flags: make sure exactly the head resident (if present) has isHead=true, others false
            Long headId = entity.getHeadResident() == null ? null : entity.getHeadResident().getResidentId();
            for (Resident r : entity.getResidents()) {
                r.setIsHead(Objects.equals(r.getResidentId(), headId));
            }

            // entity is managed — JPA will flush changes at transaction commit
            return ApartmentDetailsMapper.toDTO(entity);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to update apartment");
        }
    }

    /**
     * DELETE /api/v1/apartments/{id}
     */
    @Transactional
    public void delete(Long id) {
        try {
            boolean removed = apartmentRepository.deleteById(id);
            if (!removed) {
                throw new BadRequestException("Apartment not found");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to delete apartment");
        }
    }
}
