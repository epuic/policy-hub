package com.endava.insurance.insurance_service.domain.model.metadata;

import com.endava.insurance.insurance_service.domain.enums.FeeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_configuration")
@Getter
@NoArgsConstructor
public class FeeConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FeeType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal percentage;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public FeeConfiguration(String name, FeeType type, BigDecimal percentage,
                            LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        this.name = name != null ? name.trim() : null;
        this.type = type;
        this.percentage = percentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }

    public void update(String name, FeeType type, BigDecimal percentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        if (name != null) this.name = name.trim();
        if (type != null) this.type = type;
        if (percentage != null) this.percentage = percentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }
}
