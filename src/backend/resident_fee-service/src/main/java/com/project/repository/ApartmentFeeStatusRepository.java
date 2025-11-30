package com.project.repository;

import com.project.entity.ApartmentFeeStatus;
import com.project.entity.Fee;
import com.project.entity.Adjustment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class ApartmentFeeStatusRepository implements PanacheRepository<ApartmentFeeStatus> {

    /**
     * Find the fee status record for a specific apartment.
     * Required by API: GET /apartment-fee-statuses/{apartment_id}.
     *
     * ApartmentFeeStatus has a shared primary key with Apartment,
     * so the Apartment's ID is the PK and FK of this table.
     */
    public ApartmentFeeStatus findByApartmentId(Long apartmentId) {
        return find("apartment.apartmentId", apartmentId).firstResult();
    }

    /**
     * Fetch only unpaid fees for this apartment.
     * (Not required by mapper, but often useful.)
     */
    public Set<Fee> findUnpaidFees(Long apartmentId) {
        ApartmentFeeStatus afs = findByApartmentId(apartmentId);
        return afs == null ? null : afs.getUnpaidFeeList();
    }

    /**
     * Fetch only paid fees (optional helper).
     */
    public Set<Fee> findPaidFees(Long apartmentId) {
        ApartmentFeeStatus afs = findByApartmentId(apartmentId);
        return afs == null ? null : afs.getPaidFeeList();
    }

    /**
     * Fetch adjustment list (optional helper).
     */
    public Set<Adjustment> findAdjustments(Long apartmentId) {
        ApartmentFeeStatus afs = findByApartmentId(apartmentId);
        return afs == null ? null : afs.getAdjustmentList();
    }

    /**
     * Fetch extra adjustments (optional helper).
     */
    public Set<Adjustment> findExtraAdjustments(Long apartmentId) {
        ApartmentFeeStatus afs = findByApartmentId(apartmentId);
        return afs == null ? null : afs.getExtraAdjustmentList();
    }

    /**
     * Create or persist a fee status record.
     * Usually used only once when an apartment is initialized.
     */
    public void createStatus(ApartmentFeeStatus status) {
        persist(status);
    }

    /**
     * Update the existing status record (PUT).
     * Panache automatically tracks entity changes in a transaction.
     */
    public ApartmentFeeStatus updateStatus(ApartmentFeeStatus status) {
        return getEntityManager().merge(status);
    }

    /**
     * Check if status record exists.
     */
    public boolean existsForApartment(Long apartmentId) {
        return count("apartment.apartmentId", apartmentId) > 0;
    }
}
