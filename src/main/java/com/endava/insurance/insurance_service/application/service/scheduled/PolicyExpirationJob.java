package com.endava.insurance.insurance_service.application.service.scheduled;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;



@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyExpirationJob {

    private final PolicyRepository policyRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void expirePolicies() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<Policy> expiredPolicies = policyRepository.findActivePoliciesExpiredBefore(PolicyStatus.ACTIVE, today);

        if (expiredPolicies.isEmpty()) {
            log.debug("No expired policies found for date: {}", today);
            return;
        }

        int expiredCount = 0;
        for (Policy policy : expiredPolicies) {
            policy.expire();
            expiredCount++;
        }

        policyRepository.saveAll(expiredPolicies);
        log.info("Expired {} policies. End date before: {}", expiredCount, today);
    }
}
