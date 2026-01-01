package com.project.resident_fee_service.repository;

import com.project.resident_fee_service.entity.AdjustmentBalance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AdjustmentBalanceRepository implements PanacheRepository<AdjustmentBalance> {



}
