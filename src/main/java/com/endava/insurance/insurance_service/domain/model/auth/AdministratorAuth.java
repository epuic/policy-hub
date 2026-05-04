package com.endava.insurance.insurance_service.domain.model.auth;

import com.endava.insurance.insurance_service.domain.model.Administrator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrator_auth")
@Getter
@NoArgsConstructor
public class AdministratorAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "administrator_id", nullable = false, unique = true)
    private Administrator administrator;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public AdministratorAuth(Administrator administrator, String email, String passwordHash) {
        this.administrator = administrator;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
