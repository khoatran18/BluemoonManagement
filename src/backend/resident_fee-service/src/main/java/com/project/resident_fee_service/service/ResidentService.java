package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.resident_fee_service.entity.Resident;
import com.project.resident_fee_service.exception.InternalServerException;
import com.project.resident_fee_service.exception.NotFoundException;

import com.project.resident_fee_service.mapper.*;

import com.project.resident_fee_service.mapper.ResidentDetailsMapper;
import com.project.resident_fee_service.mapper.ResidentListResponseMapper;
import com.project.resident_fee_service.mapper.ResidentMutationMapper;
import com.project.resident_fee_service.repository.ResidentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ResidentService {

    @Inject
    ResidentRepository residentRepository;

    //////////////////////////////
    // GET LIST
    //////////////////////////////

    public ResidentListResponseDTO getResidentsByFilter(
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email,
            int page,
            int limit
    ) {
        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<Resident> list = residentRepository.getByFilter(
                    apartmentId,
                    fullName,
                    phoneNumber,
                    email,
                    queryPage,
                    queryLimit
            );

            long count = residentRepository.countByFilter(
                    apartmentId,
                    fullName,
                    phoneNumber,
                    email
            );
            return ResidentListResponseMapper.toDTO(queryPage, queryLimit, count, list);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // GET DETAIL
    //////////////////////////////

    public ResidentDetailsDTO getResidentById(Long id) {
        try {
            Resident entity = residentRepository.findWithApartment(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            return ResidentDetailsMapper.toDTO(entity);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // CREATE
    //////////////////////////////

    @Transactional
    public void createResident(ResidentCreateDTO dto) {
        try {
            // Convert DTO → Entity (no apartment passed)
            Resident entity = ResidentMutationMapper.toEntity(dto, null);

            residentRepository.persist(entity);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // UPDATE
    //////////////////////////////

    @Transactional
    public void updateResident(Long id, ResidentUpdateDTO dto) {
        try {
            Resident entity = residentRepository.findById(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            // update only personal info
            ResidentMutationMapper.updateEntity(entity, dto);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // DELETE
    //////////////////////////////

    @Transactional
    public void deleteResident(Long id) {
        try {
            Resident entity = residentRepository.findById(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            residentRepository.delete(entity);

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
