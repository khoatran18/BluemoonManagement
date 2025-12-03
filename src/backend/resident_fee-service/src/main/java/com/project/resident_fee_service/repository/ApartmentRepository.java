package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.Adjustment;
import com.project.resident_fee_service.entity.Apartment;
import com.project.resident_fee_service.entity.Resident;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class ApartmentRepository implements PanacheRepository<Apartment> {

    /**
     * Get all apartments
     */
    public List<Apartment> getAll() {
        return listAll();
    }

    /**
     * Filter + pagination (simple Panache version)
     */
    public List<Apartment> getByFilter(
            String building,
            String roomNumber,
            Long headResidentId,
            int page,
            int limit
    ) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (building != null && !building.isBlank()) {
            clauses.add("building LIKE :building");
            params.put("building", "%" + building + "%");
        }

        if (roomNumber != null && !roomNumber.isBlank()) {
            clauses.add("roomNumber LIKE :roomNumber");
            params.put("roomNumber", "%" + roomNumber + "%");
        }

        if (headResidentId != null) {
            clauses.add("headResident.residentId = :headResidentId");
            params.put("headResidentId", headResidentId);
        }

        PanacheQuery<Apartment> query;

        if (clauses.isEmpty()) {
            query = findAll();
        } else {
            String where = String.join(" AND ", clauses);
            query = find(where, params);
        }

        return query.page(Page.of(page - 1, limit)).list();
    }

    /**
     * Count for pagination
     */
    public long countByFilter(
            String building,
            String roomNumber,
            Long headResidentId
    ) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (building != null && !building.isBlank()) {
            clauses.add("building LIKE :building");
            params.put("building", "%" + building + "%");
        }

        if (roomNumber != null && !roomNumber.isBlank()) {
            clauses.add("roomNumber LIKE :roomNumber");
            params.put("roomNumber", "%" + roomNumber + "%");
        }

        if (headResidentId != null) {
            clauses.add("headResident.residentId = :headResidentId");
            params.put("headResidentId", headResidentId);
        }

        if (clauses.isEmpty()) {
            return count();
        } else {
            String where = String.join(" AND ", clauses);
            return count(where, params);
        }
    }

    /**
     * Find apartment by ID and also load its residents
     * (simple version, NO JPQL fetch join)
     */
    public Apartment findWithResidents(Long apartmentId) {
        Apartment apt = findById(apartmentId);
        if (apt == null) return null;

        // force-load residents in a separate query
        apt.setResidents(
                findResidents(apartmentId)
        );

        return apt;
    }

    /**
     * Get all residents of an apartment
     */
    public List<Resident> findResidents(Long apartmentId) {
        return getEntityManager()
                .createQuery(
                        """
                        SELECT r FROM Resident r
                        WHERE r.apartment.apartmentId = :id
                        ORDER BY r.residentId
                        """,
                        Resident.class
                )
                .setParameter("id", apartmentId)
                .getResultList();
    }

    public List<Adjustment> findAllSpecificAdjustments(Long apartmentId)
    {
        Apartment apartment = findById(apartmentId);

        return apartment.getAdjustments();
    }
}
