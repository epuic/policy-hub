package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.mapper.broker.BrokerMapper;
import com.endava.insurance.insurance_service.application.service.contract.BrokerService;
import com.endava.insurance.insurance_service.application.validator.broker.BrokerValidator;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import com.endava.insurance.insurance_service.domain.model.auth.BrokerAuth;
import com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrokerServiceImpl implements BrokerService {

    private static final String BROKER_NOT_FOUND_MSG = "Broker not found with id: ";

    private final BrokerRepository brokerRepository;
    private final BrokerAuthRepository brokerAuthRepository;
    private final BrokerMapper brokerMapper;
    private final BrokerValidator brokerValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public BrokerResponseDTO create(BrokerCreateDTO request) throws ValidationException {
        brokerValidator.validateNewBroker(request);

        Broker broker = brokerMapper.toEntity(request);
        Broker saved = brokerRepository.save(broker);

        BrokerAuth auth = new BrokerAuth(saved, request.email(), passwordEncoder.encode(request.password()));
        brokerAuthRepository.save(auth);

        log.info("Broker created: id={}, brokerCode={}", saved.getId(), saved.getBrokerCode());
        return brokerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BrokerResponseDTO update(Long id, BrokerUpdateDTO request) throws ResourceNotFoundException, ValidationException {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BROKER_NOT_FOUND_MSG + id));

        brokerValidator.validateBrokerUpdate(broker, request);
        brokerMapper.updateEntityFromRequest(request, broker);
        Broker saved = brokerRepository.save(broker);

        brokerAuthRepository.findByBrokerId(id).ifPresent(auth -> {
            if (!auth.getEmail().equals(request.email().trim())) {
                auth.updateEmail(request.email().trim());
                brokerAuthRepository.save(auth);
            }
        });

        log.info("Broker updated: id={}", saved.getId());
        return brokerMapper.toResponse(saved);
    }

    @Override
    public BrokerResponseDTO getById(Long id) throws ResourceNotFoundException {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BROKER_NOT_FOUND_MSG + id));

        return brokerMapper.toResponse(broker);
    }

    @Override
    public Page<BrokerResponseDTO> getAll(Pageable pageable) {
        return brokerRepository.findAll(pageable).map(brokerMapper::toResponse);
    }
    

    @Override
    @Transactional
    public BrokerResponseDTO activate(Long id) throws ResourceNotFoundException, ValidationException {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BROKER_NOT_FOUND_MSG + id));

        broker.activate();
        Broker saved = brokerRepository.save(broker);

        log.info("Broker activated: id={}, brokerCode={}", saved.getId(), saved.getBrokerCode());
        return brokerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BrokerResponseDTO deactivate(Long id) throws ResourceNotFoundException, ValidationException {
        Broker broker = brokerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BROKER_NOT_FOUND_MSG + id));

        broker.deactivate();
        Broker saved = brokerRepository.save(broker);

        log.info("Broker deactivated: id={}, brokerCode={}", saved.getId(), saved.getBrokerCode());
        return brokerMapper.toResponse(saved);
    }
}
