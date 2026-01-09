package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.DeleteFeeHistory;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DeleteFeeHistoryRepository
        implements PanacheRepository<DeleteFeeHistory> {

    /**
     * Get all delete fee history
     */
    public List<DeleteFeeHistory> getAll() {
        return listAll();
    }

    /**
     * Get delete fee history with filter + pagination
     */
    public List<DeleteFeeHistory> getByFilter(
            Long feeId,
            Long feeTypeId,
            int page,
            int limit) {

        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (feeId != null) {
            clauses.add("feeId = :feeId");
            params.put("feeId", feeId);
        }

        if (feeTypeId != null) {
            clauses.add("feeTypeId = :feeTypeId");
            params.put("feeTypeId", feeTypeId);
        }

        PanacheQuery<DeleteFeeHistory> query;
        if (clauses.isEmpty()) {
            query = findAll();
        } else {
            query = find(String.join(" AND ", clauses), params);
        }

        return query.page(Page.of(page - 1, limit)).list();
    }

    /**
     * Count records by filter
     */
    public long countByFilter(Long feeId, Long feeTypeId) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (feeId != null) {
            clauses.add("feeId = :feeId");
            params.put("feeId", feeId);
        }

        if (feeTypeId != null) {
            clauses.add("feeTypeId = :feeTypeId");
            params.put("feeTypeId", feeTypeId);
        }

        if (clauses.isEmpty()) {
            return count();
        }
        return count(String.join(" AND ", clauses), params);
    }

    /**
     * Create history record
     */
    public void create(DeleteFeeHistory history) {
        persist(history);
    }
}
