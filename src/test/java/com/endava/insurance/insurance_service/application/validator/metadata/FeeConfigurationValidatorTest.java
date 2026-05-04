package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.domain.enums.FeeType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeeConfigurationValidator")
class FeeConfigurationValidatorTest {

    @InjectMocks
    private FeeConfigurationValidator validator;

    @Test
    @DisplayName("validateNewFeeConfiguration when effectiveTo before effectiveFrom – throws")
    void effectiveToBeforeEffectiveFrom_throws() {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee",
                FeeType.BROKER_COMMISSION,
                new BigDecimal("5"),
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 1, 1),
                true
        );
        assertThatThrownBy(() -> validator.validateNewFeeConfiguration(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("EffectiveTo date must be on or after EffectiveFrom");
    }

    @Test
    @DisplayName("validateNewFeeConfiguration when effectiveTo after effectiveFrom – does not throw")
    void effectiveToAfterEffectiveFrom_doesNotThrow() {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee",
                FeeType.BROKER_COMMISSION,
                new BigDecimal("5"),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                true
        );
        assertThatCode(() -> validator.validateNewFeeConfiguration(request)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateFeeConfigurationUpdate when dates valid – does not throw")
    void validateUpdate_validDates_doesNotThrow() {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee",
                FeeType.BROKER_COMMISSION,
                new BigDecimal("5"),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                true
        );
        assertThatCode(() -> validator.validateFeeConfigurationUpdate(request)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateFeeConfigurationUpdate when effectiveTo before effectiveFrom – throws")
    void validateUpdate_effectiveToBeforeEffectiveFrom_throws() {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee", FeeType.ADMIN_FEE, new BigDecimal("5"),
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 1, 1), true);
        assertThatThrownBy(() -> validator.validateFeeConfigurationUpdate(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("EffectiveTo date must be on or after EffectiveFrom");
    }

    @Test
    @DisplayName("validateNewFeeConfiguration when effectiveFrom or effectiveTo null – does not throw")
    void oneDateNull_doesNotThrow() {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee", FeeType.BROKER_COMMISSION, new BigDecimal("5"),
                LocalDate.of(2025, 1, 1), null, true);
        assertThatCode(() -> validator.validateNewFeeConfiguration(request)).doesNotThrowAnyException();
    }
}
