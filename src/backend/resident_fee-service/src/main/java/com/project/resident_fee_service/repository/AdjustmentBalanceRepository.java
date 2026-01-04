package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.AdjustmentBalance;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AdjustmentBalanceRepository implements PanacheRepository<AdjustmentBalance> {



    /**
     * Get all AdjustmentBalance records
     */
    public List<AdjustmentBalance> getAll() {
        return listAll();
    }

    /**
     * Get AdjustmentBalance records with optional filer and pagination
     */
    public List<AdjustmentBalance> getByFilter(
            Long apartmentID,
            Long feeID,
            Long adjustmentID,
            int page,
            int limit) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (apartmentID != null) {
            clauses.add("apartmentID = :apartmentID");
            params.put("apartmentID", apartmentID);
        }
        if (feeID != null) {
            clauses.add("feeID = :feeID");
            params.put("feeID", feeID);
        }
        if (feeID != null) {
            clauses.add("adjustmentID = :adjustmentID");
            params.put("adjustmentID", adjustmentID);
        }

        // Build final query
        PanacheQuery<AdjustmentBalance> panacheQuery;
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
    public long countByFilter(Long apartmentID,
                              Long feeID,
                              Long adjustmentID
                              ) {

        // Prepare for query
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // Valid filters
        if (apartmentID != null) {
            clauses.add("apartmentID = :apartmentID");
            params.put("apartmentID", apartmentID);
        }
        if (feeID != null) {
            clauses.add("feeID = :feeID");
            params.put("feeID", feeID);
        }
        if (feeID != null) {
            clauses.add("adjustmentID = :adjustmentID");
            params.put("adjustmentID", adjustmentID);
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
     * Persist a new AdjustmentBalance record
     */
    public void create(AdjustmentBalance adjustmentBalance) {
        persist(adjustmentBalance);
    }

    /**
     *  Update a AdjustmentBalance record if existed
     */
    public void update(AdjustmentBalance adjustmentBalance) {
        AdjustmentBalance entity = findById(adjustmentBalance.getAdjustmentBalanceID());
        if (entity != null) {
            entity.setApartmentID(adjustmentBalance.getApartmentID());
            entity.setFeeID(adjustmentBalance.getFeeID());
            entity.setAdjustmentID(adjustmentBalance.getAdjustmentID());
            entity.setOldBalance(adjustmentBalance.getOldBalance());
            entity.setNewBalance(adjustmentBalance.getNewBalance());
            entity.setAdjustmentBalanceNote(entity.getAdjustmentBalanceNote());
        }
    }


}
