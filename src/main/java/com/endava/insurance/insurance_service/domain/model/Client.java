package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Entity
@Getter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType type;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String identificationNumber;

    private String email;
    private String phone;
    private String address;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Building> buildings;

    protected Client() {
    }

    public List<Building> getBuildings() {
        return buildings == null ? List.of() : Collections.unmodifiableList(buildings);
    }

    public Client(String countryCode, ClientType type, String name, String identificationNumber, String email, String phone, String address) throws ValidationException {
        validateType(type);
        validateCountryCode(countryCode);
        validateName(name);
        validateIdentificationNumberPresent(identificationNumber);
        validateEmail(email);
        validatePhone(phone);

        this.type = type;
        this.countryCode = countryCode.trim().toUpperCase();
        this.name = name.trim();
        this.identificationNumber = identificationNumber.trim();
        this.email = email.trim();
        this.phone = phone.trim();
        this.address = address;
    }

    public void updateDetails(String name, String email, String phone, String address) throws ValidationException {
        validateName(name);
        validateEmail(email);
        validatePhone(phone);

        this.name = name.trim();
        this.email = email.trim();
        this.phone = phone.trim();
        this.address = address;
    }

    private void validateType(ClientType type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("Client type is required");
        }
    }

    private void validateCountryCode(String countryCode) throws ValidationException {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new ValidationException("Country code is required (e.g. RO, DE)");
        }
        if (countryCode.trim().length() < 2 || countryCode.trim().length() > 3) {
            throw new ValidationException("Country code must be 2 or 3 characters (e.g. RO, DE)");
        }
    }

    private void validateIdentificationNumberPresent(String identificationNumber) throws ValidationException {
        if (identificationNumber == null || identificationNumber.isBlank()) {
            throw new ValidationException("Identification number is required");
        }
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
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone number is required");
        }
        if (!phone.matches("^\\+?[0-9.]{10,15}$")) {
            throw new ValidationException("Invalid phone number format");
        }
    }
}
