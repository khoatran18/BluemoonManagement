package com.project.repository;

import com.project.entity.Apartment;
import com.project.entity.Resident;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ApartmentRepository implements PanacheRepository<Apartment> {


    /**
     * Search + pagination for apartments.
     * Deterministic ordering added for stable paging.
     */
    public List<Apartment> search(
            String building,
            String roomNumber,
            Long headResidentId,
            int page,
            int limit
    ) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);

        Map<String, Object> params = new HashMap<>();
        List<String> clauses = new ArrayList<>();

        if (building != null && !building.isBlank()) {
            clauses.add("building = :building");
            params.put("building", building);
        }
        if (roomNumber != null && !roomNumber.isBlank()) {
            clauses.add("roomNumber = :roomNumber");
            params.put("roomNumber", roomNumber);
        }
        if (headResidentId != null) {
            clauses.add("headResident.residentId = :headResidentId");
            params.put("headResidentId", headResidentId);
        }

        // ORDER BY added for deterministic pagination
        String order = " ORDER BY apartmentId";

        if (clauses.isEmpty()) {
            return findAll(Sort.by("apartmentId"))
                    .page(Page.of(safePage - 1, safeLimit))
                    .list();
        } else {
            String query = String.join(" AND ", clauses) + order;
            return find(query, params)
                    .page(Page.of(safePage - 1, safeLimit))
                    .list();
        }
    }

    /**
     * Count total apartments matching the filters.
     */
    public long countSearch(String building, String roomNumber, Long headResidentId) {
        Map<String, Object> params = new HashMap<>();
        List<String> clauses = new ArrayList<>();

        if (building != null && !building.isBlank()) {
            clauses.add("building = :building");
            params.put("building", building);
        }
        if (roomNumber != null && !roomNumber.isBlank()) {
            clauses.add("roomNumber = :roomNumber");
            params.put("roomNumber", roomNumber);
        }
        if (headResidentId != null) {
            clauses.add("headResident.residentId = :headResidentId");
            params.put("headResidentId", headResidentId);
        }

        if (clauses.isEmpty()) {
            return count();
        } else {
            String query = String.join(" AND ", clauses);
            return count(query, params);
        }
    }

    /**
     * Load apartment with its resident list using a single query.
     * Prevents lazy-init exceptions & N+1 problems.
     */

    public Apartment findWithResidents(Long id) {
        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT a FROM Apartment a " +
                                    "LEFT JOIN FETCH a.residents " +
                                    "WHERE a.apartmentId = :id",
                            Apartment.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        }
    }


    /**
     * Load all residents from a given apartment.
     */
    public List<Resident> findResidents(Long apartmentId) {
        return getEntityManager()
                .createQuery(
                        "SELECT r FROM Resident r " +
                                "WHERE r.apartment.apartmentId = :id " +
                                "ORDER BY r.residentId",
                        Resident.class)
                .setParameter("id", apartmentId)
                .getResultList();
    }
}
