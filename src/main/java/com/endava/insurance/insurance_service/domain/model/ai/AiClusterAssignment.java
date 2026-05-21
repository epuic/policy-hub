package com.endava.insurance.insurance_service.domain.model.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "ai_cluster_assignment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"target", "entity_id"})
)
@Getter
@NoArgsConstructor
public class AiClusterAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AiClusterTarget target;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "cluster_id", nullable = false)
    private Integer clusterId;

    @Column(name = "cluster_label", nullable = false, length = 200)
    private String clusterLabel;

    @Column(precision = 12, scale = 6)
    private BigDecimal distance;

    @Column(nullable = false, length = 50)
    private String algorithm;

    @Column(name = "run_at", nullable = false)
    private LocalDateTime runAt;

    public AiClusterAssignment(AiClusterTarget target, Long entityId, Integer clusterId,
                               String clusterLabel, BigDecimal distance, String algorithm) {
        this.target = target;
        this.entityId = entityId;
        update(clusterId, clusterLabel, distance, algorithm);
    }

    public void update(Integer clusterId, String clusterLabel, BigDecimal distance, String algorithm) {
        this.clusterId = clusterId;
        this.clusterLabel = clusterLabel;
        this.distance = distance;
        this.algorithm = algorithm;
        this.runAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
