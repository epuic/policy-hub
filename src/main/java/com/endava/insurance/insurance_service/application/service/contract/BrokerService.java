package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrokerService {

    BrokerResponseDTO create(BrokerCreateDTO request) throws ValidationException;

    BrokerResponseDTO update(Long id, BrokerUpdateDTO request) throws ResourceNotFoundException, ValidationException;

    BrokerResponseDTO getById(Long id) throws ResourceNotFoundException;

    Page<BrokerResponseDTO> getAll(Pageable pageable);

    BrokerResponseDTO activate(Long id) throws ResourceNotFoundException, ValidationException;

    BrokerResponseDTO deactivate(Long id) throws ResourceNotFoundException, ValidationException;
}
