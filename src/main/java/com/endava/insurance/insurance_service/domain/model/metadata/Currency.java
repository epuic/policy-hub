package com.endava.insurance.insurance_service.domain.model.metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "currency", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@NoArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "exchange_rate_to_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal exchangeRateToBase;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Currency(String code, String name, BigDecimal exchangeRateToBase, boolean active) {
        this.code = code != null ? code.trim().toUpperCase() : null;
        this.name = name != null ? name.trim() : null;
        this.exchangeRateToBase = exchangeRateToBase;
        this.active = active;
    }

    public void update(String name, BigDecimal exchangeRateToBase, boolean active) {
        if (name != null) this.name = name.trim();
        if (exchangeRateToBase != null) this.exchangeRateToBase = exchangeRateToBase;
        this.active = active;
    }
}
