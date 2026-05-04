package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CurrencyService {

    CurrencyResponseDTO create(CurrencyRequestDTO request) throws ValidationException;

    CurrencyResponseDTO update(Long id, CurrencyRequestDTO request) throws ResourceNotFoundException, ValidationException;

    CurrencyResponseDTO getById(Long id) throws ResourceNotFoundException;

    Page<CurrencyResponseDTO> getAll(Pageable pageable);
}
