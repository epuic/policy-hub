package com.endava.insurance.insurance_service.application.service.scheduled;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyExpirationJob")
class PolicyExpirationJobTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyExpirationJob job;

    @Test
    @DisplayName("expirePolicies when no expired policies does not save")
    void expirePolicies_noExpired_doesNotSave() {
        when(policyRepository.findActivePoliciesExpiredBefore(PolicyStatus.ACTIVE, LocalDate.now(ZoneOffset.UTC)))
                .thenReturn(List.of());

        job.expirePolicies();

        verify(policyRepository).findActivePoliciesExpiredBefore(PolicyStatus.ACTIVE, LocalDate.now(ZoneOffset.UTC));
        verify(policyRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("expirePolicies when has expired policies calls expire and saveAll")
    void expirePolicies_hasExpired_expiresAndSaves() {
        Policy policy = mock(Policy.class);
        when(policyRepository.findActivePoliciesExpiredBefore(PolicyStatus.ACTIVE, LocalDate.now(ZoneOffset.UTC)))
                .thenReturn(List.of(policy));

        job.expirePolicies();

        verify(policy).expire();
        verify(policyRepository).saveAll(anyList());
    }
}
