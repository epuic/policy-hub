package com.endava.insurance.insurance_service.domain.model.metadata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Currency domain")
class CurrencyTest {

    @Test
    @DisplayName("constructor trims and uppercases code")
    void constructor_trimsAndUppercasesCode() {
        Currency currency = new Currency("  eur  ", "Euro", BigDecimal.ONE, true);
        assertThat(currency.getCode()).isEqualTo("EUR");
        assertThat(currency.getName()).isEqualTo("Euro");
        assertThat(currency.getExchangeRateToBase()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(currency.isActive()).isTrue();
    }

    @Test
    @DisplayName("constructor with null code keeps null")
    void constructor_nullCode_keepsNull() {
        Currency currency = new Currency(null, "Name", BigDecimal.ONE, false);
        assertThat(currency.getCode()).isNull();
        assertThat(currency.getName()).isEqualTo("Name");
        assertThat(currency.isActive()).isFalse();
    }

    @Test
    @DisplayName("update sets name, exchangeRate and active")
    void update_setsFields() {
        Currency currency = new Currency("EUR", "Euro", new BigDecimal("1.0"), true);
        currency.update("Euro Updated", new BigDecimal("1.1"), false);
        assertThat(currency.getName()).isEqualTo("Euro Updated");
        assertThat(currency.getExchangeRateToBase()).isEqualByComparingTo("1.1");
        assertThat(currency.isActive()).isFalse();
    }

    @Test
    @DisplayName("update with null name does not change name")
    void update_nullName_doesNotChangeName() {
        Currency currency = new Currency("EUR", "Euro", BigDecimal.ONE, true);
        currency.update(null, new BigDecimal("2.0"), true);
        assertThat(currency.getName()).isEqualTo("Euro");
        assertThat(currency.getExchangeRateToBase()).isEqualByComparingTo("2.0");
    }
}
