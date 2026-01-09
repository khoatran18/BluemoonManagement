package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.DeleteApartmentHistory;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DeleteApartmentHistoryRepository
        implements PanacheRepository<DeleteApartmentHistory> {

    /**
     * Get all delete apartment history
     */
    public List<DeleteApartmentHistory> getAll() {
        return listAll();
    }

    /**
     * Get delete apartment history with filter + pagination
     */
    public List<DeleteApartmentHistory> getByFilter(
            Long apartmentId,
            int page,
            int limit) {

        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            clauses.add("apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }

        PanacheQuery<DeleteApartmentHistory> query;
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
    public long countByFilter(Long apartmentId) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            clauses.add("apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }

        if (clauses.isEmpty()) {
            return count();
        }
        return count(String.join(" AND ", clauses), params);
    }

    /**
     * Create history record
     */
    public void create(DeleteApartmentHistory history) {
        persist(history);
    }
}
