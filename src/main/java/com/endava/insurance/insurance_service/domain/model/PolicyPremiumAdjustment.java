package com.endava.insurance.insurance_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "policy_premium_adjustment")
@Getter
@NoArgsConstructor
public class PolicyPremiumAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal percentage;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public PolicyPremiumAdjustment(Policy policy, String category, String label,
                                   BigDecimal percentage, BigDecimal amount) {
        this.policy = policy;
        this.category = category;
        this.label = label;
        this.percentage = percentage;
        this.amount = amount;
    }
}
