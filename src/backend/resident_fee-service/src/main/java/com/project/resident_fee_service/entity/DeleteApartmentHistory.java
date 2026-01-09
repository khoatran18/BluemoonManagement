package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DeleteApartmentHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteApartmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HistoryID")
    private Long historyId;

    @Column(name = "ApartmentID", nullable = true)
    private Long apartmentId;

    @Column(name = "Building", length = 30, nullable = true)
    private String building;

    @Column(name = "RoomNumber", length = 6, nullable = true)
    private String roomNumber;

    @Column(name = "DeletedAt", nullable = false)
    private LocalDateTime deletedAt;
}
