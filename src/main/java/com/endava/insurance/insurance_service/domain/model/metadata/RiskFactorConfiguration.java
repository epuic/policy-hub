package com.endava.insurance.insurance_service.domain.model.metadata;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "risk_factor_configuration")
@Getter
@NoArgsConstructor
public class RiskFactorConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskFactorConfigLevel level;


    @Column(name = "reference_id", length = 50)
    private String referenceId;

    @Column(name = "adjustment_percentage", nullable = false, precision = 12, scale = 2)
    private BigDecimal adjustmentPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public RiskFactorConfiguration(RiskFactorConfigLevel level, String referenceId,
                                   BigDecimal adjustmentPercentage, boolean active) {
        this.level = level;
        this.referenceId = referenceId != null ? referenceId.trim() : null;
        this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }

    public void update(String referenceId, BigDecimal adjustmentPercentage, boolean active) {
        if (referenceId != null) this.referenceId = referenceId.trim();
        if (adjustmentPercentage != null) this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }
}
