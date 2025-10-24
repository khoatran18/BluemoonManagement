package com.project.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApartmentSpecificAdjustmentId implements Serializable {
    private Long apartmentId;
    private Long adjustmentId;
}
