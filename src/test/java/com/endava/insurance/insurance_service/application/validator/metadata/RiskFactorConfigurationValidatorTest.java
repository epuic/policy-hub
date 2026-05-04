package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskFactorConfigurationValidator")
class RiskFactorConfigurationValidatorTest {

    @Mock
    private com.endava.insurance.insurance_service.application.validator.metadata.secondary.RiskFactorConfigurationExistenceValidator existenceValidator;

    @InjectMocks
    private RiskFactorConfigurationValidator validator;

    @Nested
    @DisplayName("validateNewRiskFactorConfiguration")
    class ValidateNew {

        @Test
        @DisplayName("CITY level with numeric referenceId and valid percentage – does not throw")
        void city_valid_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "1", new BigDecimal("5"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("CITY level with blank referenceId – throws")
        void city_blankReferenceId_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "  ", new BigDecimal("5"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Reference ID is required");
        }

        @Test
        @DisplayName("CITY level with non-numeric referenceId – throws")
        void city_nonNumericReferenceId_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "abc", new BigDecimal("5"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("numeric ID");
        }

        @Test
        @DisplayName("BUILDING_TYPE with valid enum – does not throw")
        void buildingType_valid_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.BUILDING_TYPE, "RESIDENTIAL", new BigDecimal("10"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("BUILDING_TYPE with invalid value – throws")
        void buildingType_invalid_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.BUILDING_TYPE, "INVALID_TYPE", new BigDecimal("10"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid building type");
        }

        @Test
        @DisplayName("adjustment percentage over 100 – throws")
        void adjustmentOver100_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "1", new BigDecimal("101"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("between -100 and 100");
        }

        @Test
        @DisplayName("adjustment percentage below -100 – throws")
        void adjustmentBelowMinus100_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "1", new BigDecimal("-101"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("between -100 and 100");
        }

        @Test
        @DisplayName("RISK_FACTOR_TYPE with valid enum – does not throw")
        void riskFactorType_valid_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.RISK_FACTOR_TYPE, "FLOOD_ZONE", new BigDecimal("10"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("RISK_FACTOR_TYPE with invalid value – throws")
        void riskFactorType_invalid_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.RISK_FACTOR_TYPE, "INVALID", new BigDecimal("10"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid risk factor type");
        }

        @Test
        @DisplayName("COUNTRY level with numeric referenceId – does not throw")
        void country_numericReferenceId_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.COUNTRY, "1", new BigDecimal("5"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("COUNTY level with numeric referenceId – does not throw")
        void county_numericReferenceId_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.COUNTY, "2", new BigDecimal("5"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null level – does not throw (early return)")
        void nullLevel_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    null, "any", new BigDecimal("5"), true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null adjustment percentage – does not throw (early return)")
        void nullAdjustmentPercentage_doesNotThrow() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "1", null, true);
            assertThatCode(() -> validator.validateNewRiskFactorConfiguration(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null referenceId for geography level – throws")
        void nullReferenceId_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, null, new BigDecimal("5"), true);
            assertThatThrownBy(() -> validator.validateNewRiskFactorConfiguration(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Reference ID is required");
        }
    }

    @Nested
    @DisplayName("validateRiskFactorConfigurationUpdate")
    class ValidateUpdate {

        @Test
        @DisplayName("delegates to validateNewRiskFactorConfiguration")
        void delegatesToValidateNew() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "1", new BigDecimal("5"), true);
            assertThatCode(() -> validator.validateRiskFactorConfigurationUpdate(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("invalid request throws")
        void invalidRequest_throws() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "abc", new BigDecimal("5"), true);
            assertThatThrownBy(() -> validator.validateRiskFactorConfigurationUpdate(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("numeric ID");
        }
    }
}
