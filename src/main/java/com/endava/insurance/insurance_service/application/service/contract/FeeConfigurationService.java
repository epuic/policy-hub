package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeeConfigurationService {

    FeeConfigurationResponseDTO create(FeeConfigurationRequestDTO request) throws ValidationException;

    FeeConfigurationResponseDTO update(Long id, FeeConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException;

    FeeConfigurationResponseDTO getById(Long id) throws ResourceNotFoundException;

    Page<FeeConfigurationResponseDTO> getAll(Pageable pageable);
}
