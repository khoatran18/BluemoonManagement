package com.project.service;

import com.project.dto.ApartmentDTO.*;
import com.project.entity.Apartment;
import com.project.entity.Resident;
import com.project.exception.InternalServerException;
import com.project.exception.NotFoundException;
import com.project.mapper.*;
import com.project.repository.ApartmentRepository;
import com.project.repository.ResidentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ApartmentService {

    @Inject
    ApartmentRepository apartmentRepository;

    @Inject
    ResidentRepository residentRepository;


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


    ///////////////////////////////// CREATE /////////////////////////////////

    /**
     * POST /api/v1/apartments
     */
    @Transactional
    public void createApartment(ApartmentCreateDTO dto) {
        try {
            Apartment entity = ApartmentMutationMapper.toEntity(dto);
            apartmentRepository.persist(entity);
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
