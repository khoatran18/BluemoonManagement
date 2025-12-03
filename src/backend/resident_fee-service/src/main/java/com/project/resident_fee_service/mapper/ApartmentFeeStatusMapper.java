package com.project.mapper;

import com.project.dto.ApartmentFeeStatusDTO;
import com.project.entity.Adjustment;
import com.project.entity.ApartmentFeeStatus;
import com.project.entity.Fee;
import com.project.repository.FeeRepository;

import java.util.HashSet;
import java.util.Set;

public class ApartmentFeeStatusMapper {

    private final FeeRepository feeRepository = new FeeRepository();

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
                dto.unpaidFees.add(feeEntityToDTO(fee));
            }
        }

        // ================= Adjustments =================
        dto.adjustments = new HashSet<>();
        if (entity.getAdjustmentList() != null) {
            for (Adjustment adj : entity.getAdjustmentList()) {
                dto.adjustments.add(adjustmentEntityToDTO(adj));
            }
        }

        // ============ Extra Adjustments ================
        dto.extraAdjustments = new HashSet<>();

        if (entity.getExtraAdjustmentList() != null) {
            for (Adjustment adj : entity.getExtraAdjustmentList()) {
                dto.extraAdjustments.add(extraAdjustmentEntityToDTO(adj));
            }
        }


        // ================= Totals ======================
        dto.totalFee = entity.getAmountDue();
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
            Set<Fee> newPaid = new HashSet<>();
            for (ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef ref : dto.paidFees) {
                if (ref.feeId != null) {
                    Fee fee = feeRepository.findById(ref.feeId);
                    if (fee != null) newPaid.add(fee);
                }
            }
            entity.setPaidFeeList(newPaid);
        }

        // ========= Unpaid Fees ==========
        if (dto.unpaidFees != null) {
            Set<Fee> newUnpaid = new HashSet<>();
            for (ApartmentFeeStatusDTO.FeeStatusUpdateDTO.FeeRef ref : dto.unpaidFees) {
                if (ref.feeId != null) {
                    Fee fee = feeRepository.findById(ref.feeId);
                    if (fee != null) newUnpaid.add(fee);
                }
            }
            entity.setUnpaidFeeList(newUnpaid);
        }

        // ========= Timestamp ==========
        entity.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
