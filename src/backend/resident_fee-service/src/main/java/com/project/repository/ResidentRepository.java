package com.project.repository;

import com.project.entity.Resident;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ResidentRepository implements PanacheRepository<Resident> {

    public List<Resident> search(
            Long apartmentId,
            String fullName,
            String phoneNumber,
            String email,
            int page,
            int limit
    ) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);

        StringBuilder q = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            q.append(" AND apartment.apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }
        if (fullName != null && !fullName.isBlank()) {
            q.append(" AND LOWER(fullName) LIKE :fullName");
            params.put("fullName", "%" + fullName.toLowerCase() + "%");
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            q.append(" AND phoneNumber LIKE :phoneNumber");
            params.put("phoneNumber", "%" + phoneNumber + "%");
        }
        if (email != null && !email.isBlank()) {
            q.append(" AND LOWER(email) LIKE :email");
            params.put("email", "%" + email.toLowerCase() + "%");
        }

        return find(q.toString(), params).page(Page.of(safePage - 1, safeLimit)).list();
    }

    public long countSearch(Long apartmentId, String fullName, String phoneNumber, String email) {
        StringBuilder q = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (apartmentId != null) {
            q.append(" AND apartment.apartmentId = :apartmentId");
            params.put("apartmentId", apartmentId);
        }
        if (fullName != null && !fullName.isBlank()) {
            q.append(" AND LOWER(fullName) LIKE :fullName");
            params.put("fullName", "%" + fullName.toLowerCase() + "%");
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            q.append(" AND phoneNumber LIKE :phoneNumber");
            params.put("phoneNumber", "%" + phoneNumber + "%");
        }
        if (email != null && !email.isBlank()) {
            q.append(" AND LOWER(email) LIKE :email");
            params.put("email", "%" + email.toLowerCase() + "%");
        }

        return count(q.toString(), params);
    }

    public Resident findWithApartment(Long residentId) {
        // Use EntityManager fetch-join and handle no-result safely
        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT r FROM Resident r LEFT JOIN FETCH r.apartment WHERE r.residentId = :id",
                            Resident.class)
                    .setParameter("id", residentId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Resident> findByApartmentId(Long apartmentId) {
        // fixed: include operator in query string
        return find("apartment.apartmentId = ?1", apartmentId).list();
    }

    public Resident findHead(Long apartmentId) {
        return find("isHead = TRUE AND apartment.apartmentId = :id", Map.of("id", apartmentId)).firstResult();
    }
}
