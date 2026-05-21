package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.PolicyPremiumAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyPremiumAdjustmentRepository extends JpaRepository<PolicyPremiumAdjustment, Long> {
    List<PolicyPremiumAdjustment> findByPolicyIdOrderByIdAsc(Long policyId);
}
