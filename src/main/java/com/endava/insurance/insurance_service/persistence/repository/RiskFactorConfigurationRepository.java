package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorConfigurationRepository extends JpaRepository<RiskFactorConfiguration, Long> {

    List<RiskFactorConfiguration> findByActiveTrueAndLevelAndReferenceId(
            RiskFactorConfigLevel level, String referenceId);
}
