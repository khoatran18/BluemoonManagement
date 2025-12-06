package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.FeeType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FeeTypeRepository implements PanacheRepository<FeeType> {

    /**
     * Get all FeeType records
     */
    public List<FeeType> getAll() {
        return listAll();
    }

}
