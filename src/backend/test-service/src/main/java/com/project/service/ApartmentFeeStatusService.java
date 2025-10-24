//package com.project.service;
//
//import com.project.dto.ApartmentFeeStatusDTO;
//import com.project.entity.Apartment;
//import com.project.entity.ApartmentFeeStatus;
//import com.project.entity.Fee;
//import com.project.exception.BadRequestException;
//import com.project.exception.NotFoundException;
//import com.project.mapper.ApartmentFeeStatusMapper;
//import com.project.repository.ApartmentFeeStatusRepository;
//import com.project.repository.ApartmentRepository;
//import com.project.repository.FeeRepository;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import jakarta.transaction.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Set;
//
//@ApplicationScoped
//public class ApartmentFeeStatusService {
//
//    @Inject
//    ApartmentFeeStatusRepository statusRepo;
//
//    @Inject
//    ApartmentRepository apartmentRepo;
//
//    @Inject
//    FeeRepository feeRepo;
//
//    @Transactional
//    public ApartmentFeeStatusDTO createStatus(Long apartmentId) {
//        Apartment apartment = apartmentRepo.findById(apartmentId);
//        if (apartment == null)
//            throw new NotFoundException("Apartment not found with id: " + apartmentId);
//
//        ApartmentFeeStatus existing = statusRepo.find("apartment.id", apartmentId).firstResult();
//        if (existing != null)
//            throw new BadRequestException("Apartment already has a fee status record");
//
//        ApartmentFeeStatus status = new ApartmentFeeStatus();
//        status.setApartment(apartment);
//        status.setTotalFee(BigDecimal.ZERO);
//        status.setTotalPaid(BigDecimal.ZERO);
//        status.setBalance(BigDecimal.ZERO);
//
//        statusRepo.persist(status);
//        return ApartmentFeeStatusMapper.toDTO(status);
//    }
//
//    @Transactional
//    public ApartmentFeeStatusDTO addUnpaidFee(Long statusId, Long feeId) {
//        ApartmentFeeStatus status = statusRepo.findById(statusId);
//        if (status == null)
//            throw new NotFoundException("Status not found with id: " + statusId);
//
//        Fee fee = feeRepo.findById(feeId);
//        if (fee == null)
//            throw new NotFoundException("Fee not found with id: " + feeId);
//
//        if (status.getUnpaidFees().contains(fee))
//            throw new BadRequestException("This fee is already unpaid for this apartment");
//
//        status.getUnpaidFees().add(fee);
//        status.setTotalFee(status.getTotalFee().add(fee.getFeeAmount()));
//        status.setBalance(status.getTotalFee().subtract(status.getTotalPaid()));
//
//        return ApartmentFeeStatusMapper.toDTO(status);
//    }
//
//    @Transactional
//    public ApartmentFeeStatusDTO markFeeAsPaid(Long statusId, Long feeId) {
//        ApartmentFeeStatus status = statusRepo.findById(statusId);
//        if (status == null)
//            throw new NotFoundException("Status not found with id: " + statusId);
//
//        Fee fee = feeRepo.findById(feeId);
//        if (fee == null)
//            throw new NotFoundException("Fee not found with id: " + feeId);
//
//        if (!status.getUnpaidFees().remove(fee))
//            throw new BadRequestException("Fee is not in unpaid list");
//
//        status.getPaidFees().add(fee);
//        status.setTotalPaid(status.getTotalPaid().add(fee.getFeeAmount()));
//        status.setBalance(status.getTotalFee().subtract(status.getTotalPaid()));
//
//        return ApartmentFeeStatusMapper.toDTO(status);
//    }
//
//    public ApartmentFeeStatusDTO getStatusByApartment(Long apartmentId) {
//        ApartmentFeeStatus status = statusRepo.find("apartment.id", apartmentId).firstResult();
//        if (status == null)
//            throw new NotFoundException("Fee status not found for apartment id: " + apartmentId);
//
//        return ApartmentFeeStatusMapper.toDTO(status);
//    }
//}