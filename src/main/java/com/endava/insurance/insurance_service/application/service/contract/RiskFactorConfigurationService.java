package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RiskFactorConfigurationService {

    RiskFactorConfigurationResponseDTO create(RiskFactorConfigurationRequestDTO request) throws ValidationException;

    RiskFactorConfigurationResponseDTO update(Long id, RiskFactorConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException;

    RiskFactorConfigurationResponseDTO getById(Long id) throws ResourceNotFoundException;

    Page<RiskFactorConfigurationResponseDTO> getAll(Pageable pageable);
}
