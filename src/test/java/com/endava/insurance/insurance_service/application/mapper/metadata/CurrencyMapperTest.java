package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CurrencyMapper")
class CurrencyMapperTest {

    private final CurrencyMapper mapper = new CurrencyMapper();

    @Test
    @DisplayName("toEntity maps request to Currency")
    void toEntity_mapsRequest() {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", new BigDecimal("1.00"), true);
        Currency currency = mapper.toEntity(request);
        assertThat(currency.getCode()).isEqualTo("EUR");
        assertThat(currency.getName()).isEqualTo("Euro");
        assertThat(currency.getExchangeRateToBase()).isEqualByComparingTo("1.00");
        assertThat(currency.isActive()).isTrue();
    }

    @Test
    @DisplayName("toResponse maps Currency to DTO")
    void toResponse_mapsToDto() {
        Currency currency = new Currency("RON", "Romanian Leu", new BigDecimal("4.97"), true);
        CurrencyResponseDTO dto = mapper.toResponse(currency);
        assertThat(dto.code()).isEqualTo("RON");
        assertThat(dto.name()).isEqualTo("Romanian Leu");
        assertThat(dto.exchangeRateToBase()).isEqualByComparingTo("4.97");
        assertThat(dto.active()).isTrue();
    }

    @Test
    @DisplayName("updateEntityFromRequest updates currency")
    void updateEntityFromRequest_updatesCurrency() {
        Currency currency = new Currency("USD", "US Dollar", new BigDecimal("1.10"), true);
        CurrencyRequestDTO request = new CurrencyRequestDTO("USD", "US Dollar Updated", new BigDecimal("1.15"), false);
        mapper.updateEntityFromRequest(request, currency);
        assertThat(currency.getName()).isEqualTo("US Dollar Updated");
        assertThat(currency.getExchangeRateToBase()).isEqualByComparingTo("1.15");
        assertThat(currency.isActive()).isFalse();
    }
}
