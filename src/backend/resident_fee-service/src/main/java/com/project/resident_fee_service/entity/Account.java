package com.project.resident_fee_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "Account",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "Username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Long accountId;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$", message = "Wrong email format")
    @Column(name = "Email", nullable = true)
    private String email;

    @Pattern(regexp = "^[0-9]{12}$", message = "Identity Number must be 12 digits")
    @Column(name = "IdentityNumber", nullable = false, length = 12)
    private String identityNumber;

    public enum RoleEnum {
        Citizen,
        FeeCollector,
        Admin
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private RoleEnum role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ResidentID",
            referencedColumnName = "ResidentID",
            foreignKey = @ForeignKey(name = "fk_account_resident")
    )
    private Resident resident;
}
