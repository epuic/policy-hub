package com.endava.insurance.insurance_service.application.validator.policy;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.validator.policy.secondary.PolicyBusinessValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyValidator")
@SuppressWarnings("java:S1130")
class PolicyValidatorTest {

    @Mock
    private PolicyBusinessValidator policyBusinessValidator;

    @InjectMocks
    private PolicyValidator validator;

    @Test
    @DisplayName("validateNewPolicy delegates to business validator for all checks")
    void validateNewPolicy_delegates() throws ResourceNotFoundException, ValidationException {
        PolicyCreateDTO request = new PolicyCreateDTO(
                1L, 2L, 3L,
                LocalDate.now(ZoneOffset.UTC).plusDays(1),
                LocalDate.now(ZoneOffset.UTC).plusDays(30),
                new BigDecimal("100"),
                1L
        );
        doNothing().when(policyBusinessValidator).ensureClientExists(1L);
        doNothing().when(policyBusinessValidator).ensureBuildingExists(2L);
        doNothing().when(policyBusinessValidator).ensureBuildingBelongsToClient(2L, 1L);
        doNothing().when(policyBusinessValidator).ensureBrokerExists(3L);
        doNothing().when(policyBusinessValidator).ensureBrokerIsActive(3L);
        doNothing().when(policyBusinessValidator).ensureCurrencyExists(1L);
        doNothing().when(policyBusinessValidator).ensureCurrencyIsActive(1L);

        assertThatCode(() -> validator.validateNewPolicy(request)).doesNotThrowAnyException();

        verify(policyBusinessValidator).ensureClientExists(1L);
        verify(policyBusinessValidator).ensureBuildingExists(2L);
        verify(policyBusinessValidator).ensureBuildingBelongsToClient(2L, 1L);
        verify(policyBusinessValidator).ensureBrokerExists(3L);
        verify(policyBusinessValidator).ensureBrokerIsActive(3L);
        verify(policyBusinessValidator).ensureCurrencyExists(1L);
        verify(policyBusinessValidator).ensureCurrencyIsActive(1L);
    }

    @Test
    @DisplayName("validateNewPolicy when client not exists – propagates ResourceNotFoundException")
    void whenClientNotExists_throws() throws ResourceNotFoundException {
        PolicyCreateDTO request = new PolicyCreateDTO(
                999L, 2L, 3L,
                LocalDate.now(ZoneOffset.UTC).plusDays(1),
                LocalDate.now(ZoneOffset.UTC).plusDays(30),
                new BigDecimal("100"),
                1L
        );
        doThrow(new ResourceNotFoundException("Client not found with id: 999"))
                .when(policyBusinessValidator).ensureClientExists(999L);

        assertThatThrownBy(() -> validator.validateNewPolicy(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found");
    }
}
