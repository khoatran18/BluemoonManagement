package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;

import com.project.resident_fee_service.mapper.ResidentDetailsMapper;
import com.project.resident_fee_service.mapper.ResidentListResponseMapper;
import com.project.resident_fee_service.mapper.ResidentMutationMapper;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.ResidentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class ResidentService {

    private static final Logger log =
            LoggerFactory.getLogger(ResidentService.class);

    @Inject
    ResidentRepository residentRepository;

    @Inject
    ApartmentRepository apartmentRepository;

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

        log.info("[Resident] [Service] getResidentsByFilter Start");
        log.info("Input: apartmentId={}, fullName={}, phoneNumber={}, email={}, page={}, limit={}",
                apartmentId, fullName, phoneNumber, email, page, limit);

        try {
            int queryPage = Math.max(page, 1);
            int queryLimit = Math.max(limit, 1);

            List<Resident> list =
                    residentRepository.getByFilter(
                            apartmentId, fullName, phoneNumber, email,
                            queryPage, queryLimit
                    );

            long count =
                    residentRepository.countByFilter(
                            apartmentId, fullName, phoneNumber, email
                    );

            ResidentListResponseDTO result =
                    ResidentListResponseMapper.toDTO(
                            queryPage, queryLimit, count, list
                    );

            log.info("[Resident] [Service] getResidentsByFilter End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Resident] [Service] getResidentsByFilter Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // GET DETAIL
    //////////////////////////////

    public ResidentDetailsDTO getResidentById(Long id) {

        log.info("[Resident] [Service] getResidentById Start");
        log.info("Input: residentId={}", id);

        try {
            Resident entity = residentRepository.findWithApartment(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            ResidentDetailsDTO result =
                    ResidentDetailsMapper.toDTO(entity);

            log.info("[Resident] [Service] getResidentById End");
            log.info("Output: {}", result);

            return result;

        } catch (Exception e) {
            log.error("[Resident] [Service] getResidentById Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // CREATE
    //////////////////////////////

    @Transactional
    public void createResident(ResidentCreateDTO dto) {

        log.info("[Resident] [Service] createResident Start");
        log.info("Input: {}", dto);

        try {
            Apartment apartment =
                    apartmentRepository.findById(dto.apartmentId);
            if (apartment == null)
                throw new NotFoundException(
                        "Apartment not found with id: " + dto.apartmentId
                );

            Resident entity =
                    ResidentMutationMapper.toEntity(dto, apartment);

            residentRepository.persist(entity);

            log.info("[Resident] [Service] createResident End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] createResident Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // UPDATE
    //////////////////////////////

    @Transactional
    public void updateResident(Long id, ResidentUpdateDTO dto) {

        log.info("[Resident] [Service] updateResident Start");
        log.info("Input: residentId={}, dto={}", id, dto);

        try {
            Resident entity = residentRepository.findById(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            ResidentMutationMapper.updateEntity(entity, dto);

            log.info("[Resident] [Service] updateResident End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] updateResident Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    //////////////////////////////
    // DELETE
    //////////////////////////////

    @Transactional
    public void deleteResident(Long id) {

        log.info("[Resident] [Service] deleteResident Start");
        log.info("Input: residentId={}", id);

        try {
            Resident entity = residentRepository.findById(id);
            if (entity == null)
                throw new NotFoundException("Resident not found with id: " + id);

            // Delete account
//            if (entity.getAccount() != null) {
//                entity.getAccount().setResident(null);
//                entity.setAccount(null);
//            }

            residentRepository.delete(entity);

            log.info("[Resident] [Service] deleteResident End");
            log.info("Output: None");

        } catch (Exception e) {
            log.error("[Resident] [Service] deleteResident Error", e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
