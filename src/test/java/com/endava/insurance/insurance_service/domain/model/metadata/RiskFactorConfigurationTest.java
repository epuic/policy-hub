package com.endava.insurance.insurance_service.domain.model.metadata;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RiskFactorConfiguration domain")
class RiskFactorConfigurationTest {

    @Test
    @DisplayName("constructor trims referenceId")
    void constructor_trimsReferenceId() {
        RiskFactorConfiguration config = new RiskFactorConfiguration(
                RiskFactorConfigLevel.CITY, "  1  ", new BigDecimal("10"), true);
        assertThat(config.getLevel()).isEqualTo(RiskFactorConfigLevel.CITY);
        assertThat(config.getReferenceId()).isEqualTo("1");
        assertThat(config.getAdjustmentPercentage()).isEqualByComparingTo("10");
        assertThat(config.isActive()).isTrue();
    }

    @Test
    @DisplayName("constructor with null referenceId keeps null")
    void constructor_nullReferenceId_keepsNull() {
        RiskFactorConfiguration config = new RiskFactorConfiguration(
                RiskFactorConfigLevel.BUILDING_TYPE, null, new BigDecimal("0"), false);
        assertThat(config.getReferenceId()).isNull();
        assertThat(config.isActive()).isFalse();
    }

    @Test
    @DisplayName("update sets referenceId, adjustmentPercentage and active")
    void update_setsFields() {
        RiskFactorConfiguration config = new RiskFactorConfiguration(
                RiskFactorConfigLevel.COUNTRY, "RO", new BigDecimal("5"), true);
        config.update("  RO  ", new BigDecimal("15"), false);
        assertThat(config.getReferenceId()).isEqualTo("RO");
        assertThat(config.getAdjustmentPercentage()).isEqualByComparingTo("15");
        assertThat(config.isActive()).isFalse();
    }

    @Test
    @DisplayName("update with null referenceId does not change referenceId")
    void update_nullReferenceId_doesNotChange() {
        RiskFactorConfiguration config = new RiskFactorConfiguration(
                RiskFactorConfigLevel.CITY, "1", new BigDecimal("5"), true);
        config.update(null, new BigDecimal("20"), true);
        assertThat(config.getReferenceId()).isEqualTo("1");
        assertThat(config.getAdjustmentPercentage()).isEqualByComparingTo("20");
    }
}
