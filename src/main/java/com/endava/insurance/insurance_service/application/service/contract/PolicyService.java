package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCancelDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PolicyService {

    PolicyResponseDTO createDraft(PolicyCreateDTO request) throws ResourceNotFoundException, ValidationException;

    PolicyResponseDTO getById(Long id) throws ResourceNotFoundException;

    Page<PolicyResponseDTO> getFiltered(Long clientId, Long brokerId, PolicyStatus status,
                                        LocalDate startDateFrom, LocalDate endDateTo,
                                        Pageable pageable);

    PolicyResponseDTO activate(Long id) throws ResourceNotFoundException, ValidationException;

    PolicyResponseDTO cancel(Long id, PolicyCancelDTO request) throws ResourceNotFoundException, ValidationException;
}
