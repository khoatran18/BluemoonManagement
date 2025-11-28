package com.project.repository;

import com.project.entity.Adjustment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AdjustmentRepository implements PanacheRepository<Adjustment> {

    /**
     * Get all Adjustment records
     */
    public List<Adjustment> getAll() {
        return listAll();
    }

    /**
     * Get Adjustment records with optional filer and pagination
     */
    public List<Adjustment> getByFilter(Long feeId,
                                 BigDecimal adjustmentAmount,
                                 Adjustment.AdjustmentType adjustmentType,
                                 LocalDate effectiveDate,
                                 LocalDate expiryDate,
                                 int page,
                                 int limit) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (feeId != null) {
            if (feeId > 0) {
                clauses.add("fee.id = :feeId");
                params.put("feeId", feeId);
            } else {
                clauses.add("fee.id = :feeId");
                params.put("feeId", null);
            }
        }
        if (adjustmentAmount != null) {
            clauses.add("adjustmentAmount = :adjustmentAmount");
            params.put("adjustmentAmount", adjustmentAmount);
        }
        if (adjustmentType != null) {
            clauses.add("adjustmentType = :adjustmentType");
            params.put("adjustmentType", adjustmentType);
        }
        if (effectiveDate != null) {
            clauses.add("startDate = :startDate");
            params.put("startDate", effectiveDate);
        }
        if (expiryDate != null) {
            clauses.add("endDate = :endDate");
            params.put("endDate", expiryDate);
        }

        // Build final query
        PanacheQuery<Adjustment> panacheQuery;
        if (clauses.isEmpty()) {
            panacheQuery = findAll();
        } else {
            String query = String.join(" AND ", clauses);
            panacheQuery = find(query, params);
        }

        // Get results
        return panacheQuery.page(Page.of(page - 1, limit)).list();
    }

    /**
     * Count number of Adjustment records matching the filters
     */
    public long countByFilter(Long feeId,
                              BigDecimal adjustmentAmount,
                              Adjustment.AdjustmentType adjustmentType,
                              LocalDate effectiveDate,
                              LocalDate expiryDate
    ) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (feeId != null) {
            // if feeId = -1, this adjustment belongs to an Apartment, not Fee
            if (feeId > 0) {
                clauses.add("fee.id = :feeId");
                params.put("feeId", feeId);
            } else {
                clauses.add("fee.id = :feeId");
                params.put("feeId", null);
            }
        }
        if (adjustmentAmount != null) {
            clauses.add("adjustmentAmount = :adjustmentAmount");
            params.put("adjustmentAmount", adjustmentAmount);
        }
        if (adjustmentType != null) {
            clauses.add("adjustmentType = :adjustmentType");
            params.put("adjustmentType", adjustmentType);
        }
        if (effectiveDate != null) {
            clauses.add("startDate = :startDate");
            params.put("startDate", effectiveDate);
        }
        if (expiryDate != null) {
            clauses.add("endDate = :endDate");
            params.put("endDate", expiryDate);
        }

        // Get results
        if (clauses.isEmpty()) {
            return count();
        } else {
            String query = String.join(" AND ", clauses);
            return count(query, params);
        }
    }

    /**
     * Persist a new Adjustment record
     */
    public void create(Adjustment adjustment) {
        persist(adjustment);
    }

    /**
     *  Update an Adjustment record if existed
     */
    public void update(Adjustment adjustment) {
        Adjustment entity = findById(adjustment.getAdjustmentId());
        if (entity != null) {
            entity.setFee(adjustment.getFee());
            entity.setAdjustmentAmount(adjustment.getAdjustmentAmount());
            entity.setAdjustmentType(adjustment.getAdjustmentType());
            entity.setReason(adjustment.getReason());
            entity.setStartDate(adjustment.getStartDate());
            entity.setEndDate(adjustment.getEndDate());
        }
    }

}
