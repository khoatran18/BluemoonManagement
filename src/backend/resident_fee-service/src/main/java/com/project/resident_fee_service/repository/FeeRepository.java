package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.Fee;
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
public class FeeRepository implements PanacheRepository<Fee> {

    /**
     * Get all Fee records
     */
    public List<Fee> getAll() {
        return listAll();
    }

    /**
     * Get Fee records with optional filer and pagination
     */
    public List<Fee> getByFilter(Long feeTypeId,
                                 Long feeCategoryId,
                                 String feeName,
                                 BigDecimal feeAmount,
                                 String applicableMonth,
                                 LocalDate effectiveDate,
                                 LocalDate expiryDate,
                                 String status,
                                 int page,
                                 int limit) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (feeTypeId != null) {
            clauses.add("feeType.id = :feeTypeId");
            params.put("feeTypeId", feeTypeId);
        }
        if (feeCategoryId != null) {
            clauses.add("feeCategory.id = :feeCategoryId");
            params.put("feeCategoryId", feeCategoryId);
        }
        if (feeName != null && !feeName.isEmpty()) {
            clauses.add("feeName = :feeName");
            params.put("feeName", feeName);
        }
        if (feeAmount != null) {
            clauses.add("amount = :amount");
            params.put("amount", feeAmount);
        }
        if (applicableMonth != null) {
            clauses.add("applicableMonth = :applicableMonth");
            params.put("applicableMonth", applicableMonth);
        }
        if (effectiveDate != null) {
            clauses.add("startDate = :startDate");
            params.put("startDate", effectiveDate);
        }
        if (expiryDate != null) {
            clauses.add("endDate = :endDate");
            params.put("endDate", expiryDate);
        }
        if (status != null) {
            clauses.add("status = :status");
            params.put("status", status);
        }

        // Build final query
        PanacheQuery<Fee> panacheQuery;
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
     * Count number of Fee records matching the filters
     */
    public long countByFilter(Long feeTypeId,
                                 Long feeCategoryId,
                                 String feeName,
                                 BigDecimal feeAmount,
                                 String applicableMonth,
                                 LocalDate effectiveDate,
                                 LocalDate expiryDate,
                                 String status) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (feeTypeId != null) {
            clauses.add("feeType.id = :feeTypeId");
            params.put("feeTypeId", feeTypeId);
        }
        if (feeCategoryId != null) {
            clauses.add("feeCategory.id = :feeCategoryId");
            params.put("feeCategoryId", feeCategoryId);
        }
        if (feeName != null && !feeName.isEmpty()) {
            clauses.add("feeName = :feeName");
            params.put("feeName", feeName);
        }
        if (feeAmount != null) {
            clauses.add("amount = :amount");
            params.put("amount", feeAmount);
        }
        if (applicableMonth != null) {
            clauses.add("applicableMonth = :applicableMonth");
            params.put("applicableMonth", applicableMonth);
        }
        if (effectiveDate != null) {
            clauses.add("startDate = :startDate");
            params.put("startDate", effectiveDate);
        }
        if (expiryDate != null) {
            clauses.add("endDate = :endDate");
            params.put("endDate", expiryDate);
        }
        if (status != null) {
            clauses.add("status = :status");
            params.put("status", status);
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
     * Persist a new Fee record
     */
    public void create(Fee fee) {
        persist(fee);
    }

    /**
     *  Update a Fee record if existed
     */
    public void update(Fee fee) {
        Fee entity = findById(fee.getFeeId());
        if (entity != null) {
            entity.setFeeType(fee.getFeeType());
            entity.setFeeCategory(fee.getFeeCategory());
            entity.setFeeName(fee.getFeeName());
            entity.setFeeDescription(fee.getFeeDescription());
            entity.setAmount(fee.getAmount());
            entity.setApplicableMonth(fee.getApplicableMonth());
            entity.setStartDate(fee.getStartDate());
            entity.setEndDate(fee.getEndDate());
            entity.setStatus(fee.getStatus());
        }
    }

}
