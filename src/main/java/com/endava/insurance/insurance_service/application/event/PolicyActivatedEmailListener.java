package com.endava.insurance.insurance_service.application.event;

import com.endava.insurance.insurance_service.application.service.email.PolicyEmailService;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyActivatedEmailListener {

    private final PolicyRepository policyRepository;
    private final PolicyEmailService policyEmailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPolicyActivated(PolicyActivatedEvent event) {
        try {
            policyRepository.findById(event.policyId()).ifPresentOrElse(
                    policyEmailService::sendActivationEmail,
                    () -> log.warn("Policy activation email skipped. Policy not found: id={}", event.policyId())
            );
        } catch (RuntimeException ex) {
            log.error("Policy activation email failed: policyId={}", event.policyId(), ex);
        }
    }
}
