package com.endava.insurance.insurance_service.domain.model.metadata;

import com.endava.insurance.insurance_service.domain.enums.FeeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FeeConfiguration domain")
class FeeConfigurationTest {

    @Test
    @DisplayName("constructor trims name")
    void constructor_trimsName() {
        FeeConfiguration fee = new FeeConfiguration(
                "  Admin Fee  ", FeeType.ADMIN_FEE, new BigDecimal("5"),
                LocalDate.of(2025, 1, 1), null, true);
        assertThat(fee.getName()).isEqualTo("Admin Fee");
        assertThat(fee.getType()).isEqualTo(FeeType.ADMIN_FEE);
        assertThat(fee.getPercentage()).isEqualByComparingTo("5");
        assertThat(fee.getEffectiveFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(fee.getEffectiveTo()).isNull();
        assertThat(fee.isActive()).isTrue();
    }

    @Test
    @DisplayName("update sets fields and trims name")
    void update_setsFieldsAndTrimsName() {
        FeeConfiguration fee = new FeeConfiguration(
                "Fee", FeeType.BROKER_COMMISSION, new BigDecimal("5"), null, null, true);
        fee.update("  Updated Fee  ", FeeType.ADMIN_FEE, new BigDecimal("10"),
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 12, 31), false);
        assertThat(fee.getName()).isEqualTo("Updated Fee");
        assertThat(fee.getType()).isEqualTo(FeeType.ADMIN_FEE);
        assertThat(fee.getPercentage()).isEqualByComparingTo("10");
        assertThat(fee.getEffectiveFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fee.getEffectiveTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fee.isActive()).isFalse();
    }

    @Test
    @DisplayName("update with null name does not change name")
    void update_nullName_doesNotChangeName() {
        FeeConfiguration fee = new FeeConfiguration(
                "Original", FeeType.ADMIN_FEE, new BigDecimal("5"), null, null, true);
        fee.update(null, null, new BigDecimal("8"), null, null, true);
        assertThat(fee.getName()).isEqualTo("Original");
        assertThat(fee.getPercentage()).isEqualByComparingTo("8");
    }
}
