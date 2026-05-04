package com.endava.insurance.insurance_service.application.validator.building;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.application.validator.client.ClientExistenceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildingValidator {

    private final ClientExistenceValidator clientExistenceValidator;
    private final CityRepository cityRepository;

    public void validateClientAndCityExist(Long clientId, Long cityId) throws ResourceNotFoundException {
        clientExistenceValidator.ensureClientExists(clientId);
        validateCityExists(cityId);
    }

    public void validateCityExists(Long cityId) throws ResourceNotFoundException {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("City not found with id: " + cityId);
        }
    }




}
