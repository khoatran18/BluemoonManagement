package com.project.repository;

import com.project.entity.FeeType;
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
