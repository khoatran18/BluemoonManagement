package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.ApartmentFeeStatusDTO;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.ApartmentFeeStatus;
import com.project.resident_fee_service.entity.Fee;
import com.project.resident_fee_service.repository.ApartmentRepository;
import com.project.resident_fee_service.repository.FeeRepository;

import java.util.HashSet;
import java.util.Set;

public class ApartmentFeeStatusMapper {

    private final FeeRepository feeRepository = new FeeRepository();
    private final ApartmentRepository  apartmentRepository = new ApartmentRepository();

    // ==========================================================
    // =============== 5.1  GET MAPPER ==========================
    // ==========================================================

    public ApartmentFeeStatusDTO.FeeStatusDTO entityToDTO(ApartmentFeeStatus entity) {

        ApartmentFeeStatusDTO.FeeStatusDTO dto =
                new ApartmentFeeStatusDTO.FeeStatusDTO();

        // Apartment ID
        dto.apartmentId = entity.getApartment() != null
                ? entity.getApartment().getApartmentId()
                : null;

        // ================= Unpaid Fees =================
        dto.unpaidFees = new HashSet<>();
        if (entity.getUnpaidFeeList() != null) {
            for (Fee fee : entity.getUnpaidFeeList()) {
                if (fee.getStatus() != Fee.FeeStatus.ACTIVE) {
                    continue;
                }
                dto.unpaidFees.add(feeEntityToDTO(fee));
            }
        }

//        // ================= Adjustments =================
//        dto.adjustments = new HashSet<>();
//        if (entity.getAdjustmentList() != null) {
//            for (Adjustment adj : entity.getAdjustmentList()) {
//                dto.adjustments.add(adjustmentEntityToDTO(adj));
//            }
//        }

        // ================= Adjustments (from FeeList) =================
        dto.adjustments = new HashSet<>();
        Set<Fee> feeSources = new HashSet<>();

        if (entity.getUnpaidFeeList() != null) {
            feeSources.addAll(entity.getUnpaidFeeList());
        }

        for (Fee fee : feeSources) {

            // Get only ACTIVE
            if (fee.getStatus() != Fee.FeeStatus.ACTIVE) {
                continue;
            }
            if (fee.getAdjustments() != null) {
                for (Adjustment adj : fee.getAdjustments()) {
                    dto.adjustments.add(adjustmentEntityToDTO(adj));
                }
            }
        }

        // ============ Extra Adjustments ================
//        dto.extraAdjustments = new HashSet<>();
//        if (entity.getExtraAdjustmentList() != null) {
//            for (Adjustment adj : entity.getExtraAdjustmentList()) {
//                dto.extraAdjustments.add(extraAdjustmentEntityToDTO(adj));
//            }
//        }
        dto.extraAdjustments = new HashSet<>();
        if (entity.getApartment() != null) {

            // load apartment with specific adjustments
            Apartment apartment = apartmentRepository.findById(entity.getApartment().getApartmentId());

            if (apartment != null && apartment.getAdjustments() != null) {
                for (Adjustment adj : apartment.getAdjustments()) {
                    dto.extraAdjustments.add(extraAdjustmentEntityToDTO(adj));
                }
            }
        }

        // ================= Totals ======================
//        dto.totalFee = entity.getAmountDue();
        dto.totalPaid = entity.getAmountPaid();
        dto.balance = entity.getBalance();
        dto.updatedAt = entity.getUpdatedAt();

        return dto;
    }


    // ==========================================================
    // =============== Fee → FeeDetails DTO =====================
    // ==========================================================

    private ApartmentFeeStatusDTO.FeeStatusDTO.FeeDetails feeEntityToDTO(Fee fee) {

        ApartmentFeeStatusDTO.FeeStatusDTO.FeeDetails dto =
                new ApartmentFeeStatusDTO.FeeStatusDTO.FeeDetails();

        dto.feeId = fee.getFeeId();
        dto.feeName = fee.getFeeName();
        dto.feeAmount = fee.getAmount();

        dto.feeTypeId = fee.getFeeType().getFeeTypeId();
        dto.feeTypeName = fee.getFeeType().getFeeTypeName();

        dto.feeCategoryId = fee.getFeeCategory().getFeeCategoryId();
        dto.feeCategoryName = fee.getFeeCategory().getFeeCategoryName();

        dto.effectiveDate = fee.getStartDate();
        dto.expiryDate = fee.getEndDate();
        dto.feeDescription = fee.getFeeDescription();

        return dto;
    }


    // ==========================================================
    // =============== Adjustment → DTO ==========================
    // ==========================================================

    private ApartmentFeeStatusDTO.FeeStatusDTO.AdjustmentDetails adjustmentEntityToDTO(Adjustment adj) {

        ApartmentFeeStatusDTO.FeeStatusDTO.AdjustmentDetails dto =
                new ApartmentFeeStatusDTO.FeeStatusDTO.AdjustmentDetails();

        dto.adjustmentId = adj.getAdjustmentId();
        dto.feeId = adj.getFee() != null ? adj.getFee().getFeeId() : null;
        dto.adjustmentAmount = adj.getAdjustmentAmount();
        dto.adjustmentType = adj.getAdjustmentType();
        dto.reason = adj.getReason();
        dto.effectiveDate = adj.getStartDate();
        dto.expiryDate = adj.getEndDate();

        return dto;
    }


    // ==========================================================
    // =============== ExtraAdjustment → DTO =====================
    // ==========================================================

    private ApartmentFeeStatusDTO.FeeStatusDTO.ExtraAdjustmentDetails extraAdjustmentEntityToDTO(Adjustment adj) {

        ApartmentFeeStatusDTO.FeeStatusDTO.ExtraAdjustmentDetails dto =
                new ApartmentFeeStatusDTO.FeeStatusDTO.ExtraAdjustmentDetails();

        dto.adjustmentId = adj.getAdjustmentId();
        dto.feeId = adj.getFee() != null ? adj.getFee().getFeeId() : null;
        dto.adjustmentAmount = adj.getAdjustmentAmount();
        dto.adjustmentType = adj.getAdjustmentType();
        dto.reason = adj.getReason();
        dto.effectiveDate = adj.getStartDate();
        dto.expiryDate = adj.getEndDate();

        return dto;
    }


    // ==========================================================
    // =============== 5.2  PUT APPLY MAPPER =====================
    // ==========================================================

    public void applyUpdateDTOToEntity(
            ApartmentFeeStatus entity,
            ApartmentFeeStatusDTO.FeeStatusUpdateDTO dto
    ) {

        // ========= Total Paid =========
        if (dto.totalPaid != null) {
            entity.setAmountPaid(dto.totalPaid);
        }

        // ========= Balance ============
        if (dto.balance != null) {
            entity.setBalance(dto.balance);
        }

        // ========= Paid Fees ==========
        if (dto.paidFees != null) {

            Set<Fee> oldPaid = entity.getPaidFeeList() != null
                    ? new HashSet<>(entity.getPaidFeeList())
                    : new HashSet<>();

            Set<Fee> newPaid = new HashSet<>();
            for (ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef ref : dto.paidFees) {
                if (ref.feeId != null) {
                    Fee fee = feeRepository.findById(ref.feeId);
                    if (fee != null) newPaid.add(fee);
                }
            }

            // Get new paidFees
            Set<Fee> addedFees = new HashSet<>(newPaid);
            addedFees.removeAll(oldPaid);

            Apartment apartment = entity.getApartment();

            // Add Apartment into all Fee.paidApartmentList
            for (Fee fee : addedFees) {
                if (fee.getPaidApartmentList() == null) {
                    fee.setPaidApartmentList(new HashSet<>());
                }
                fee.getPaidApartmentList().add(apartment);
            }

            entity.setPaidFeeList(newPaid);
        }

        // ========= Unpaid Fees ==========
//        if (dto.unpaidFees != null) {
//            Set<Fee> newUnpaid = new HashSet<>();
//            for (ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef ref : dto.unpaidFees) {
//                if (ref.feeId != null) {
//                    Fee fee = feeRepository.findById(ref.feeId);
//                    if (fee != null) newUnpaid.add(fee);
//                }
//            }
//            entity.setUnpaidFeeList(newUnpaid);
//        }

        // ========= Timestamp ==========
        entity.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
