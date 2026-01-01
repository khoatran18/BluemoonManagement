package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.PayHistory;
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
public class PayHistoryRepository implements PanacheRepository<PayHistory> {


    /**
     * Get all PayHistory records
     */
    public List<PayHistory> getAll() {
        return listAll();
    }

    /**
     * Get PayHistory records with optional filer and pagination
     */
    public List<PayHistory> getByFilter(
                                 Long apartmentID,
                                 Long feeID,
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

        // Build final query
        PanacheQuery<PayHistory> panacheQuery;
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
                              Long feeID) {

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

        // Get results
        if (clauses.isEmpty()) {
            return count();
        } else {
            String query = String.join(" AND ", clauses);
            return count(query, params);
        }
    }

    /**
     * Persist a new PayHistory record
     */
    public void create(PayHistory payHistory) {
        persist(payHistory);
    }

    /**
     *  Update a PayHistory record if existed
     */
    public void update(PayHistory payHistory) {
        PayHistory entity = findById(payHistory.getPayHistoryID());
        if (entity != null) {
            entity.setApartmentID(payHistory.getApartmentID());
            entity.setFeeID(payHistory.getFeeID());
            entity.setPayDateTime(payHistory.getPayDateTime());
            entity.setPayAmount(payHistory.getPayAmount());
            entity.setPayNote(payHistory.getPayNote());
        }
    }

}
