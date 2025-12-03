package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.Resident;
import com.project.resident_fee_service.entity.Apartment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class ResidentRepository implements PanacheRepository<Resident> {

    /**
     * Get all residents
     */
    public List<Resident> getAll() {
        return listAll();
    }

    /**
     * Filter + pagination (simple Panache version)
     */
    public List<Resident> getByFilter(
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email,
            int page,
            int limit
    ) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            clauses.add("apartment.apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }

        if (fullName != null && !fullName.isBlank()) {
            clauses.add("LOWER(fullName) LIKE :fullName");
            params.put("fullName", "%" + fullName.toLowerCase() + "%");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            clauses.add("phoneNumber LIKE :phoneNumber");
            params.put("phoneNumber", "%" + phoneNumber + "%");
        }

        if (email != null && !email.isBlank()) {
            clauses.add("LOWER(email) LIKE :email");
            params.put("email", "%" + email.toLowerCase() + "%");
        }

        PanacheQuery<Resident> query;

        if (clauses.isEmpty()) {
            query = findAll();
        } else {
            String where = String.join(" AND ", clauses);
            query = find(where, params);
        }

        return query.page(Page.of(page - 1, limit)).list();
    }

    /**
     * Count matching rows
     */
    public long countByFilter(
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email
    ) {
        List<String> clauses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            clauses.add("apartment.apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }

        if (fullName != null && !fullName.isBlank()) {
            clauses.add("LOWER(fullName) LIKE :fullName");
            params.put("fullName", "%" + fullName.toLowerCase() + "%");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            clauses.add("phoneNumber LIKE :phoneNumber");
            params.put("phoneNumber", "%" + phoneNumber + "%");
        }

        if (email != null && !email.isBlank()) {
            clauses.add("LOWER(email) LIKE :email");
            params.put("email", "%" + email.toLowerCase() + "%");
        }

        if (clauses.isEmpty()) {
            return count();
        } else {
            String where = String.join(" AND ", clauses);
            return count(where, params);
        }
    }

    /**
     * Load resident with Apartment info
     */
    public Resident findWithApartment(Long id) {
        Resident resident = findById(id);
        if (resident == null) return null;

        // Force load apartment if lazy (simple version)
        Apartment apt = resident.getApartment();
        if (apt != null) {
            apt.getApartmentId(); // touch property to avoid LazyInitializationException in some cases
        }

        return resident;
    }

    /**
     * Get all residents of an apartment
     */
    public List<Resident> findByApartmentId(Long apartmentId) {
        return find("apartment.apartmentId = ?1 ORDER BY residentId", apartmentId).list();
    }
}
