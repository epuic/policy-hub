package com.endava.insurance.insurance_service.application.validator.report;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyReportValidator")
class PolicyReportValidatorTest {

    @InjectMocks
    private PolicyReportValidator validator;

    @Test
    @DisplayName("validate when from and to valid – does not throw")
    void validate_validRange_doesNotThrow() {
        PolicyReportFilter filter = new PolicyReportFilter(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                PolicyStatus.ACTIVE,
                "RON",
                BuildingType.RESIDENTIAL
        );
        assertThatCode(() -> validator.validate(filter)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate when from equals to – does not throw")
    void validate_sameDate_doesNotThrow() {
        PolicyReportFilter filter = new PolicyReportFilter(
                LocalDate.of(2025, 6, 15),
                LocalDate.of(2025, 6, 15),
                null,
                null,
                null
        );
        assertThatCode(() -> validator.validate(filter)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate when only from – does not throw")
    void validate_onlyFrom_doesNotThrow() {
        PolicyReportFilter filter = new PolicyReportFilter(
                LocalDate.of(2025, 1, 1),
                null,
                null,
                null,
                null
        );
        assertThatCode(() -> validator.validate(filter)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate when only to – does not throw")
    void validate_onlyTo_doesNotThrow() {
        PolicyReportFilter filter = new PolicyReportFilter(
                null,
                LocalDate.of(2025, 12, 31),
                null,
                null,
                null
        );
        assertThatCode(() -> validator.validate(filter)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate when all null – does not throw")
    void validate_allNull_doesNotThrow() {
        PolicyReportFilter filter = new PolicyReportFilter(null, null, null, null, null);
        assertThatCode(() -> validator.validate(filter)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate when from after to – throws ValidationException")
    void validate_fromAfterTo_throws() {
        PolicyReportFilter filter = new PolicyReportFilter(
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2025, 1, 1),
                null,
                null,
                null
        );
        assertThatThrownBy(() -> validator.validate(filter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("'from' must be on or before 'to'");
    }
}
