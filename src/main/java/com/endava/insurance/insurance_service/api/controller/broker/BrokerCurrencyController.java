package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/brokers/currencies")
@RequiredArgsConstructor
public class BrokerCurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<Page<CurrencyResponseDTO>> getAll(
            @PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(currencyService.getAll(pageable));
    }
}
