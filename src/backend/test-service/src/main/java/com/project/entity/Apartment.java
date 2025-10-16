package com.project.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "apartments")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 30, nullable = false)
    private String building;

    @Column(length = 6, nullable = false)
    private String roomNumber;

    @OneToOne(mappedBy = "apartment", cascade = CascadeType.ALL)
    private ApartmentFeeStatus feeStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Getter and Setter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ApartmentFeeStatus getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(ApartmentFeeStatus feeStatus) {
        this.feeStatus = feeStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
