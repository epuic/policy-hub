package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorRepository extends JpaRepository<RiskFactor, Long> {

    List<RiskFactor> findByTypeIn(List<RiskFactorType> types);
}
