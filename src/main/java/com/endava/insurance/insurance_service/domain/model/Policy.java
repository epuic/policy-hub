package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "policies", uniqueConstraints = @UniqueConstraint(columnNames = "policy_number"))
@Getter
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false, unique = true, length = 50)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    private Broker broker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status = PolicyStatus.DRAFT;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePremiumAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "final_premium", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalPremium;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    protected Policy() {
    }

    public Policy(String policyNumber, PolicyParties parties,
                  LocalDate startDate, LocalDate endDate, BigDecimal basePremiumAmount,
                  Currency currency, BigDecimal finalPremium) throws ValidationException {
        validatePolicyNumber(policyNumber);
        validateClient(parties.client());
        validateBuilding(parties.building());
        validateBroker(parties.broker());
        validateDates(startDate, endDate);
        validateBasePremiumAmount(basePremiumAmount);
        validateCurrency(currency);
        validateFinalPremium(finalPremium);

        this.policyNumber = policyNumber.trim();
        this.client = parties.client();
        this.building = parties.building();
        this.broker = parties.broker();
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePremiumAmount = basePremiumAmount;
        this.currency = currency;
        this.finalPremium = finalPremium;
        this.status = PolicyStatus.DRAFT;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.lastUpdatedAt = now;
    }

    public record PolicyParties(Client client, Building building, Broker broker) {}


    public void activate() throws ValidationException {
        if (this.status != PolicyStatus.DRAFT) {
            throw new ValidationException("Only draft policies can be activated");
        }
        if (startDate != null && startDate.isBefore(LocalDate.now(ZoneOffset.UTC))) {
            throw new ValidationException("Cannot activate policy with start date in the past");
        }
        this.status = PolicyStatus.ACTIVE;
        this.lastUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void cancel(String reason) throws ValidationException {
        if (this.status != PolicyStatus.ACTIVE) {
            throw new ValidationException("Only active policies can be cancelled");
        }
        validateCancellationReason(reason);
        this.status = PolicyStatus.CANCELLED;
        this.cancellationDate = LocalDate.now(ZoneOffset.UTC);
        this.cancellationReason = reason.trim();
        this.lastUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void expire() {
        if (this.status == PolicyStatus.ACTIVE && this.endDate != null && this.endDate.isBefore(LocalDate.now(ZoneOffset.UTC))) {
            this.status = PolicyStatus.EXPIRED;
            this.lastUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }


    private void validateCancellationReason(String reason) throws ValidationException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Cancellation reason is required");
        }
        if (reason.trim().length() > 1000) {
            throw new ValidationException("Cancellation reason must be at most 1000 characters");
        }
    }

    private void validatePolicyNumber(String policyNumber) throws ValidationException {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new ValidationException("Policy number is required");
        }
        if (policyNumber.trim().length() > 50) {
            throw new ValidationException("Policy number must be at most 50 characters");
        }
    }

    private void validateClient(Client client) throws ValidationException {
        if (client == null) {
            throw new ValidationException("Client is required");
        }
    }

    private void validateBuilding(Building building) throws ValidationException {
        if (building == null) {
            throw new ValidationException("Building is required");
        }
    }

    private void validateBroker(Broker broker) throws ValidationException {
        if (broker == null) {
            throw new ValidationException("Broker is required");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) throws ValidationException {
        if (startDate == null) {
            throw new ValidationException("Start date is required");
        }
        if (endDate == null) {
            throw new ValidationException("End date is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be on or after start date");
        }
    }

    private void validateBasePremiumAmount(BigDecimal basePremiumAmount) throws ValidationException {
        if (basePremiumAmount == null) {
            throw new ValidationException("Base premium amount is required");
        }
        if (basePremiumAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Base premium amount must be greater than 0");
        }
    }

    private void validateCurrency(Currency currency) throws ValidationException {
        if (currency == null) {
            throw new ValidationException("Currency is required");
        }
    }

    private void validateFinalPremium(BigDecimal finalPremium) throws ValidationException {
        if (finalPremium == null) {
            throw new ValidationException("Final premium is required");
        }
        if (finalPremium.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Final premium must be non-negative");
        }
    }
}
