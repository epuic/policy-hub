package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCancelDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.policy.PolicyMapper;
import com.endava.insurance.insurance_service.application.service.contract.PolicyService;
import com.endava.insurance.insurance_service.application.service.premium.PolicyPremiumCalculator;
import com.endava.insurance.insurance_service.application.validator.policy.PolicyValidator;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyServiceImpl implements PolicyService {

    private static final String BUILDING_NOT_FOUND_MSG = "Building not found with id: ";
    private static final String POLICY_NOT_FOUND_MSG = "Policy not found with id: ";

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final PolicyValidator policyValidator;
    private final PolicyPremiumCalculator premiumCalculator;
    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public PolicyResponseDTO createDraft(PolicyCreateDTO request) throws ResourceNotFoundException, ValidationException {
        policyValidator.validateNewPolicy(request);

        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND_MSG + request.buildingId()));

        String policyNumber = generatePolicyNumber();
        BigDecimal finalPremium = premiumCalculator.calculateFinalPremium(
                request.basePremiumAmount(),
                building,
                request.startDate()
        );

        Policy policy = policyMapper.toEntity(request, policyNumber, finalPremium);
        Policy saved = policyRepository.save(policy);

        log.info("Policy draft created: id={}, policyNumber={}, basePremium={}, finalPremium={}", 
                saved.getId(), saved.getPolicyNumber(), saved.getBasePremiumAmount(), saved.getFinalPremium());
        return policyMapper.toResponse(saved);
    }

    @Override
    public PolicyResponseDTO getById(Long id) throws ResourceNotFoundException {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POLICY_NOT_FOUND_MSG + id));

        return policyMapper.toResponse(policy);
    }

    @Override
    public Page<PolicyResponseDTO> getFiltered(Long clientId, Long brokerId, PolicyStatus status,
                                               LocalDate startDateFrom, LocalDate endDateTo,
                                               Pageable pageable) {
        return policyRepository.findFiltered(clientId, brokerId, status, startDateFrom, endDateTo, pageable)
                .map(policyMapper::toResponse);
    }

    @Override
    @Transactional
    public PolicyResponseDTO activate(Long id) throws ResourceNotFoundException, ValidationException {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POLICY_NOT_FOUND_MSG + id));

        policy.activate();
        Policy saved = policyRepository.save(policy);

        log.info("Policy activated: id={}, policyNumber={}", saved.getId(), saved.getPolicyNumber());
        return policyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PolicyResponseDTO cancel(Long id, PolicyCancelDTO request) throws ResourceNotFoundException, ValidationException {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POLICY_NOT_FOUND_MSG + id));

        policy.cancel(request.reason());
        Policy saved = policyRepository.save(policy);

        log.info("Policy cancelled: id={}, policyNumber={}", saved.getId(), saved.getPolicyNumber());
        return policyMapper.toResponse(saved);
    }

    private String generatePolicyNumber() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return "POL-" + suffix;
    }
}
