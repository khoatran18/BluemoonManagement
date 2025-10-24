//package com.project.mapper;
//
//import com.project.dto.FeeDTO;
//import com.project.entity.Fee;
//
//public class FeeMapper {
//    public static Fee toEntity(FeeDTO dto) {
//        Fee entity = new Fee();
//        entity.setFeeName(dto.feeName);
//        entity.setFeeAmount(dto.feeAmount);
//        entity.setApplicableMonth(dto.applicableMonth);
//        entity.setEffectiveDate(dto.effectiveDate);
//        entity.setExpiryDate(dto.expiryDate);
//        entity.setStatus(dto.status);
//        return entity;
//    }
//
//    public static FeeDTO toDTO(Fee entity) {
//        FeeDTO dto = new FeeDTO();
//        dto.id = entity.getId();
//        dto.feeName = entity.getFeeName();
//        dto.feeAmount = entity.getFeeAmount();
//        dto.applicableMonth = entity.getApplicableMonth();
//        dto.effectiveDate = entity.getEffectiveDate();
//        dto.expiryDate = entity.getExpiryDate();
//        dto.status = entity.getStatus();
//        return dto;
//    }
//}
