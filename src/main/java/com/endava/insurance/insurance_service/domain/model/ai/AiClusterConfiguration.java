package com.endava.insurance.insurance_service.domain.model.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "ai_cluster_configuration",
        uniqueConstraints = @UniqueConstraint(columnNames = {"target", "cluster_id"})
)
@Getter
@NoArgsConstructor
public class AiClusterConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AiClusterTarget target;

    @Column(name = "cluster_id", nullable = false)
    private Integer clusterId;

    @Column(nullable = false, length = 200)
    private String label;

    @Column(name = "adjustment_percentage", nullable = false, precision = 12, scale = 2)
    private BigDecimal adjustmentPercentage = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public AiClusterConfiguration(AiClusterTarget target, Integer clusterId, String label,
                                  BigDecimal adjustmentPercentage, boolean active) {
        this.target = target;
        this.clusterId = clusterId;
        this.label = label;
        this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }

    public void update(String label, BigDecimal adjustmentPercentage, boolean active) {
        if (label != null && !label.isBlank()) {
            this.label = label.trim();
        }
        if (adjustmentPercentage != null) {
            this.adjustmentPercentage = adjustmentPercentage;
        }
        this.active = active;
    }
}
