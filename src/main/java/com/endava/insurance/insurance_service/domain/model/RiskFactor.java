package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "risk_factor", uniqueConstraints = @UniqueConstraint(columnNames = "type"))
@Getter
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true)
    private RiskFactorType type;

    @ManyToMany(mappedBy = "riskFactors")
    private Set<Building> buildings = new HashSet<>();

    protected RiskFactor() {
    }

    public RiskFactor(RiskFactorType type) {
        if (type == null) {
            throw new IllegalArgumentException("Risk factor type is required");
        }
        this.type = type;
    }
}
