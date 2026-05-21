package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "brokers")
@Getter
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_code", nullable = false, unique = true, length = 50)
    private String brokerCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrokerStatus status = BrokerStatus.ACTIVE;

    @Column(name = "commission_percentage", precision = 12, scale = 2)
    private BigDecimal commissionPercentage;

    protected Broker() {
    }

    public Broker(String brokerCode, String name, String email, String phone,
                  BrokerStatus status, BigDecimal commissionPercentage) throws ValidationException {
        validateBrokerCode(brokerCode);
        validateName(name);
        validateEmailWhenPresent(email);
        validatePhoneWhenPresent(phone);
        validateStatus(status);
        validateCommissionPercentageWhenPresent(commissionPercentage);

        this.brokerCode = brokerCode.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.phone = phone != null ? phone.trim() : null;
        this.status = status;
        this.commissionPercentage = commissionPercentage;
    }

    public void updateDetails(String name, String email, String phone,
                             BigDecimal commissionPercentage) throws ValidationException {
        validateName(name);
        validateEmailWhenPresent(email);
        validatePhoneWhenPresent(phone);
        validateCommissionPercentageWhenPresent(commissionPercentage);

        this.name = name.trim();
        this.email = email.trim();
        this.phone = phone != null ? phone.trim() : null;
        this.commissionPercentage = commissionPercentage;
    }

    public void activate() throws ValidationException {
        if (this.status == BrokerStatus.ACTIVE) {
            throw new ValidationException("Broker is already active");
        }
        this.status = BrokerStatus.ACTIVE;
    }

    public void deactivate() throws ValidationException {
        if (this.status == BrokerStatus.INACTIVE) {
            throw new ValidationException("Broker is already inactive");
        }
        this.status = BrokerStatus.INACTIVE;
    }

    private void validateBrokerCode(String brokerCode) throws ValidationException {
        if (brokerCode == null || brokerCode.trim().isEmpty()) {
            throw new ValidationException("Broker code is required");
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

    private void validateEmailWhenPresent(String email) throws ValidationException {
        if (email != null && !email.trim().isEmpty() && !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private void validatePhoneWhenPresent(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty() && !phone.trim().matches("^\\+?[0-9.]{10,15}$")) {
            throw new ValidationException("Invalid phone number format");
        }
    }

    private void validateStatus(BrokerStatus status) throws ValidationException {
        if (status == null) {
            throw new ValidationException("Status is required");
        }
    }

    private void validateCommissionPercentageWhenPresent(BigDecimal commissionPercentage) throws ValidationException {
        if (commissionPercentage != null) {
            if (commissionPercentage.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Commission percentage must be non-negative");
            }
            if (commissionPercentage.compareTo(new BigDecimal("100")) > 0) {
                throw new ValidationException("Commission percentage must not exceed 100");
            }
        }
    }
}
