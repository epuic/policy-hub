package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.CurrencyMapper;
import com.endava.insurance.insurance_service.application.service.contract.CurrencyService;
import com.endava.insurance.insurance_service.application.validator.metadata.CurrencyValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final CurrencyValidator currencyValidator;

    @Override
    @Transactional
    public CurrencyResponseDTO create(CurrencyRequestDTO request) throws ValidationException {
        currencyValidator.validateNewCurrency(request);

        Currency currency = currencyMapper.toEntity(request);
        Currency saved = currencyRepository.save(currency);

        log.info("Currency created: id={}, code={}", saved.getId(), saved.getCode());
        return currencyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CurrencyResponseDTO update(Long id, CurrencyRequestDTO request) throws ResourceNotFoundException, ValidationException {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        currencyValidator.validateCurrencyUpdate(currency, request);

        if (!request.active() && currency.isActive()) {
            currencyValidator.validateCurrencyDeactivation(currency);
        }

        currencyMapper.updateEntityFromRequest(request, currency);
        Currency saved = currencyRepository.save(currency);

        log.info("Currency updated: id={}", saved.getId());
        return currencyMapper.toResponse(saved);
    }

    @Override
    public CurrencyResponseDTO getById(Long id) throws ResourceNotFoundException {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        return currencyMapper.toResponse(currency);
    }

    @Override
    public Page<CurrencyResponseDTO> getAll(Pageable pageable) {
        return currencyRepository.findAll(pageable).map(currencyMapper::toResponse);
    }
}
