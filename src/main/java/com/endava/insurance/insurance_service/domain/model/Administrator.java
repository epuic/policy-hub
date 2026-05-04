package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.AdministratorRole;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "administrators")
@Getter
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdministratorRole role;

    protected Administrator() {
    }

    public Administrator(String name, String email, AdministratorRole role) throws ValidationException {
        validateName(name);
        validateEmail(email);
        validateRole(role);

        this.name = name.trim();
        this.email = email.trim();
        this.role = role;
    }

    public void updateDetails(String name, String email, AdministratorRole role) throws ValidationException {
        validateName(name);
        validateEmail(email);
        validateRole(role);

        this.name = name.trim();
        this.email = email.trim();
        this.role = role;
    }

    private void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (name.trim().length() < 2 || name.trim().length() > 20) {
            throw new ValidationException("Name must be between 2 and 20 characters");
        }
    }

    private void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private void validateRole(AdministratorRole role) throws ValidationException {
        if (role == null) {
            throw new ValidationException("Role is required");
        }
    }
}
