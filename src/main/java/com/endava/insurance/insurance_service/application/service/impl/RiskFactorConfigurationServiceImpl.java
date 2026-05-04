package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.RiskFactorConfigurationMapper;
import com.endava.insurance.insurance_service.application.service.contract.RiskFactorConfigurationService;
import com.endava.insurance.insurance_service.application.validator.metadata.RiskFactorConfigurationValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskFactorConfigurationServiceImpl implements RiskFactorConfigurationService {

    private final RiskFactorConfigurationRepository riskFactorConfigurationRepository;
    private final RiskFactorConfigurationMapper riskFactorConfigurationMapper;
    private final RiskFactorConfigurationValidator riskFactorConfigurationValidator;

    @Override
    @Transactional
    public RiskFactorConfigurationResponseDTO create(RiskFactorConfigurationRequestDTO request) throws ValidationException {
        riskFactorConfigurationValidator.validateNewRiskFactorConfiguration(request);

        RiskFactorConfiguration riskFactorConfiguration = riskFactorConfigurationMapper.toEntity(request);
        RiskFactorConfiguration saved = riskFactorConfigurationRepository.save(riskFactorConfiguration);

        log.info("Risk factor configuration created: id={}, level={}", saved.getId(), saved.getLevel());
        return riskFactorConfigurationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RiskFactorConfigurationResponseDTO update(Long id, RiskFactorConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException {
        riskFactorConfigurationValidator.validateRiskFactorConfigurationUpdate(request);

        RiskFactorConfiguration riskFactorConfiguration = riskFactorConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Risk factor configuration not found with id: " + id));

        riskFactorConfigurationMapper.updateEntityFromRequest(request, riskFactorConfiguration);
        RiskFactorConfiguration saved = riskFactorConfigurationRepository.save(riskFactorConfiguration);

        log.info("Risk factor configuration updated: id={}", saved.getId());
        return riskFactorConfigurationMapper.toResponse(saved);
    }

    @Override
    public RiskFactorConfigurationResponseDTO getById(Long id) throws ResourceNotFoundException {
        RiskFactorConfiguration riskFactorConfiguration = riskFactorConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Risk factor configuration not found with id: " + id));

        return riskFactorConfigurationMapper.toResponse(riskFactorConfiguration);
    }

    @Override
    public Page<RiskFactorConfigurationResponseDTO> getAll(Pageable pageable) {
        return riskFactorConfigurationRepository.findAll(pageable).map(riskFactorConfigurationMapper::toResponse);
    }
}
