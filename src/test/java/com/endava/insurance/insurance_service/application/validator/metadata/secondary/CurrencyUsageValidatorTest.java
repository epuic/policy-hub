package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyUsageValidator")
@SuppressWarnings("java:S1130")
class CurrencyUsageValidatorTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private CurrencyUsageValidator validator;

    @Test
    @DisplayName("ensureNotUsedInActivePolicies when not used – does not throw")
    void whenNotUsed_doesNotThrow() throws ValidationException {
        when(policyRepository.existsByCurrencyIdAndStatus(1L, PolicyStatus.ACTIVE)).thenReturn(false);
        assertThatCode(() -> validator.ensureNotUsedInActivePolicies(1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureNotUsedInActivePolicies when used in active policy – throws")
    void whenUsedInActive_throws() {
        when(policyRepository.existsByCurrencyIdAndStatus(1L, PolicyStatus.ACTIVE)).thenReturn(true);
        assertThatThrownBy(() -> validator.ensureNotUsedInActivePolicies(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot deactivate currency");
    }
}
