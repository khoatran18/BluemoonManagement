package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DeleteResidentHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteResidentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HistoryID")
    private Long historyId;

    @Column(name = "ResidentID", nullable = false)
    private Long residentId;

    @Column(name = "ApartmentID")
    private Long apartmentId;

    @Column(name = "FullName", length = 30, nullable = false)
    private String fullName;

    @Column(name = "PhoneNumber", length = 10)
    private String phoneNumber;

    @Column(name = "Email", length = 40)
    private String email;


    @Column(name = "IsHead")
    private Boolean isHead;

    @Column(name = "DeletedAt", nullable = false)
    private LocalDateTime deletedAt;
}
