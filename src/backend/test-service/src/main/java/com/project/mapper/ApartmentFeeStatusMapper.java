//package com.project.mapper;
//
//import com.project.dto.ApartmentFeeStatusDTO;
//import com.project.entity.ApartmentFeeStatus;
//
//import java.util.stream.Collectors;
//
//public class ApartmentFeeStatusMapper {
//
//    public static ApartmentFeeStatusDTO toDTO(ApartmentFeeStatus entity) {
//        ApartmentFeeStatusDTO dto = new ApartmentFeeStatusDTO();
//        dto.id = entity.getId();
//        dto.apartmentId = entity.getApartment().getId();
//        dto.unpaidFeeIds = entity.getUnpaidFees().stream()
//                .map(f -> f.getId())
//                .collect(Collectors.toSet());
//        dto.paidFeeIds = entity.getPaidFees().stream()
//                .map(f -> f.getId())
//                .collect(Collectors.toSet());
//        dto.totalFee = entity.getTotalFee();
//        dto.totalPaid = entity.getTotalPaid();
//        dto.balance = entity.getBalance();
//        return dto;
//    }
//}
