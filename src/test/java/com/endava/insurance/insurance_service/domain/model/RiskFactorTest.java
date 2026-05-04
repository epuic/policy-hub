package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RiskFactor domain")
class RiskFactorTest {

    @Test
    @DisplayName("constructor with valid type creates risk factor")
    void constructor_validType_creates() {
        RiskFactor riskFactor = new RiskFactor(RiskFactorType.FLOOD_ZONE);
        assertThat(riskFactor.getType()).isEqualTo(RiskFactorType.FLOOD_ZONE);
    }

    @Test
    @DisplayName("constructor with null type throws IllegalArgumentException")
    void constructor_nullType_throws() {
        assertThatThrownBy(() -> new RiskFactor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Risk factor type is required");
    }
}
