package com.endava.insurance.insurance_service.persistence.repository.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.endava.insurance.insurance_service.domain.model.ai.AiClusterConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiClusterConfigurationRepository extends JpaRepository<AiClusterConfiguration, Long> {

    Optional<AiClusterConfiguration> findByTargetAndClusterId(AiClusterTarget target, Integer clusterId);

    Optional<AiClusterConfiguration> findByTargetAndClusterIdAndActiveTrue(AiClusterTarget target, Integer clusterId);

    List<AiClusterConfiguration> findByTargetOrderByClusterIdAsc(AiClusterTarget target);
}
