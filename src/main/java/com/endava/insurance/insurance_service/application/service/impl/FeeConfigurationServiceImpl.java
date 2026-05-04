package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.FeeConfigurationMapper;
import com.endava.insurance.insurance_service.application.service.contract.FeeConfigurationService;
import com.endava.insurance.insurance_service.application.validator.metadata.FeeConfigurationValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.FeeConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeConfigurationServiceImpl implements FeeConfigurationService {

    private final FeeConfigurationRepository feeConfigurationRepository;
    private final FeeConfigurationMapper feeConfigurationMapper;
    private final FeeConfigurationValidator feeConfigurationValidator;

    @Override
    @Transactional
    public FeeConfigurationResponseDTO create(FeeConfigurationRequestDTO request) throws ValidationException {
        feeConfigurationValidator.validateNewFeeConfiguration(request);

        FeeConfiguration feeConfiguration = feeConfigurationMapper.toEntity(request);
        FeeConfiguration saved = feeConfigurationRepository.save(feeConfiguration);

        log.info("Fee configuration created: id={}, name={}", saved.getId(), saved.getName());
        return feeConfigurationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public FeeConfigurationResponseDTO update(Long id, FeeConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException {
        FeeConfiguration feeConfiguration = feeConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee configuration not found with id: " + id));

        feeConfigurationValidator.validateFeeConfigurationUpdate(request);
        feeConfigurationMapper.updateEntityFromRequest(request, feeConfiguration);
        FeeConfiguration saved = feeConfigurationRepository.save(feeConfiguration);

        log.info("Fee configuration updated: id={}", saved.getId());
        return feeConfigurationMapper.toResponse(saved);
    }

    @Override
    public FeeConfigurationResponseDTO getById(Long id) throws ResourceNotFoundException {
        FeeConfiguration feeConfiguration = feeConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee configuration not found with id: " + id));

        return feeConfigurationMapper.toResponse(feeConfiguration);
    }

    @Override
    public Page<FeeConfigurationResponseDTO> getAll(Pageable pageable) {
        return feeConfigurationRepository.findAll(pageable).map(feeConfigurationMapper::toResponse);
    }
}
