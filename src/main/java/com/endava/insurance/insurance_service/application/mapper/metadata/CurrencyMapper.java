package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public Currency toEntity(CurrencyRequestDTO request) {
        return new Currency(
                request.code(),
                request.name(),
                request.exchangeRateToBase(),
                request.active()
        );
    }

    public CurrencyResponseDTO toResponse(Currency currency) {
        return new CurrencyResponseDTO(
                currency.getId(),
                currency.getCode(),
                currency.getName(),
                currency.getExchangeRateToBase(),
                currency.isActive()
        );
    }

    public void updateEntityFromRequest(CurrencyRequestDTO request, Currency currency) {
        currency.update(
                request.name(),
                request.exchangeRateToBase(),
                request.active()
        );
    }
}
