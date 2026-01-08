package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.FeeCategory;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FeeCategoryRepository implements PanacheRepository<FeeCategory> {

    /**
     * Get all FeeCategory records
     */
    public List<FeeCategory> getAll() {
        return listAll();
    }

    /**
     * Get FeeCategory records with optional filer and pagination
     */
    public List<FeeCategory> getByFilter(int page,
                                         int limit) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters

        // Build final query
        PanacheQuery<FeeCategory> panacheQuery;
        if (clauses.isEmpty()) {
            panacheQuery = findAll();
        } else {
            String query = String.join(" AND ", clauses);
            panacheQuery = find(query, params);
        }

        // Get result
        return panacheQuery.page(Page.of(page - 1, limit)).list();
    }

    /**
     * Count number of FeeCategory records matching the filters
     */
    public Long countByFilter() {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
//        if (feeTypeId != null) {
//            clauses.add("feeType.id = :feeTypeId");
//            params.put("feeTypeId", feeTypeId);
//        }

        // Get results
        PanacheQuery<FeeCategory> panacheQuery;
        if (clauses.isEmpty()) {
            return count();
        } else {
            String query = String.join(" AND ", clauses);
            return count(query, params);
        }
    }

    /**
     * Persist a new FeeCategory record
     */
    public void create(FeeCategory feeCategory) {
        persist(feeCategory);
    }

    /**
     *  Update a FeeCategory record if existed
     */
    public void update(FeeCategory feeCategory) {

        FeeCategory entity = findById(feeCategory.getFeeCategoryId());
        if (entity != null) {
//            entity.setFeeType(feeCategory.getFeeType());
            entity.setFeeCategoryDescription(feeCategory.getFeeCategoryDescription());
            entity.setFeeCategoryName(feeCategory.getFeeCategoryName());
        }
    }

}
