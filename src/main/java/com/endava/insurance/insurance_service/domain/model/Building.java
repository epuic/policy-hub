package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client owner;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private Integer constructionYear;

    @Enumerated(EnumType.STRING)
    private BuildingType type;

    private Integer numberOfFloors;
    private Double surfaceArea;

    @Column(nullable = false)
    private Double insuredValue;

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "building_risk_factor",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "risk_factor_id")
    )
    private Set<RiskFactor> riskFactors = new HashSet<>();

    protected Building() {
    }


    public Building(Client owner, City city, String street, String number, BuildingAttributes attrs) throws ValidationException {
        validateOwner(owner);
        validateCity(city);
        validateStreet(street);
        validateNumber(number);
        validateType(attrs.type());
        validateConstructionYear(attrs.constructionYear());
        validateNumberOfFloors(attrs.numberOfFloors());
        validateSurfaceArea(attrs.surfaceArea());
        validateInsuredValue(attrs.insuredValue());

        this.owner = owner;
        this.city = city;
        this.street = street.trim();
        this.number = number.trim();
        this.constructionYear = attrs.constructionYear();
        this.type = attrs.type();
        this.numberOfFloors = attrs.numberOfFloors();
        this.surfaceArea = attrs.surfaceArea();
        this.insuredValue = attrs.insuredValue();
    }

    public void updateDetails(String street, String number, City city, BuildingAttributes attrs) throws ValidationException {
        validateStreet(street);
        validateNumber(number);
        validateCity(city);
        validateConstructionYear(attrs.constructionYear());
        validateType(attrs.type());
        validateNumberOfFloors(attrs.numberOfFloors());
        validateSurfaceArea(attrs.surfaceArea());
        validateInsuredValue(attrs.insuredValue());

        this.street = street.trim();
        this.number = number.trim();
        this.city = city;
        this.constructionYear = attrs.constructionYear();
        this.type = attrs.type();
        this.numberOfFloors = attrs.numberOfFloors();
        this.surfaceArea = attrs.surfaceArea();
        this.insuredValue = attrs.insuredValue();
    }

    public static record BuildingAttributes(
            Integer constructionYear,
            BuildingType type,
            Integer numberOfFloors,
            Double surfaceArea,
            Double insuredValue
    ) {}

    private void validateOwner(Client owner) throws ValidationException {
        if (owner == null) {
            throw new ValidationException("Owner (client) is required");
        }
    }

    private void validateCity(City city) throws ValidationException {
        if (city == null) {
            throw new ValidationException("City is required");
        }
    }

    private void validateStreet(String street) throws ValidationException {
        if (street == null || street.trim().isEmpty()) {
            throw new ValidationException("Street is required");
        }
    }

    private void validateNumber(String number) throws ValidationException {
        if (number == null || number.trim().isEmpty()) {
            throw new ValidationException("Number is required");
        }
    }

    private void validateType(BuildingType type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("Building type is required");
        }
    }

    private void validateConstructionYear(Integer constructionYear) throws ValidationException {
        if (constructionYear != null && constructionYear < 1800) {
            throw new ValidationException("Construction year must be valid (at least 1800)");
        }
    }

    private void validateNumberOfFloors(Integer numberOfFloors) throws ValidationException {
        if (numberOfFloors != null && numberOfFloors <= 0) {
            throw new ValidationException("Number of floors must be positive");
        }
    }

    private void validateSurfaceArea(Double surfaceArea) throws ValidationException {
        if (surfaceArea != null && surfaceArea <= 0) {
            throw new ValidationException("Surface area must be greater than 0");
        }
    }

    private void validateInsuredValue(Double insuredValue) throws ValidationException {
        if (insuredValue == null) {
            throw new ValidationException("Insured value is required");
        }
        if (insuredValue <= 0) {
            throw new ValidationException("Insured value must be greater than 0");
        }
    }

    public Set<RiskFactor> getRiskFactors() {
        return riskFactors == null ? Set.of() : Collections.unmodifiableSet(riskFactors);
    }

    public void replaceRiskFactors(Collection<RiskFactor> factors) {
        if (this.riskFactors == null) {
            this.riskFactors = new HashSet<>();
        }
        this.riskFactors.clear();
        if (factors != null) {
            this.riskFactors.addAll(factors);
        }
    }
}