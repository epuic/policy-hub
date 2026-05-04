package com.endava.insurance.insurance_service.application.validator.building;

import com.endava.insurance.insurance_service.application.validator.client.ClientExistenceValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingValidator - unit tests")
class BuildingValidatorTest {

    @Mock
    private ClientExistenceValidator clientExistenceValidator;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private BuildingValidator validator;

    @Test
    @DisplayName("missing city (non-existent cityId) should fail")
    void validateClientAndCityExist_cityDoesNotExist_throws() throws ResourceNotFoundException {
        Long clientId = 1L;
        Long cityId = 999L;
        when(cityRepository.existsById(cityId)).thenReturn(false);
        doNothing().when(clientExistenceValidator).ensureClientExists(clientId);

        assertThatThrownBy(() -> validator.validateClientAndCityExist(clientId, cityId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("City not found")
                .hasMessageContaining("999");
        verify(clientExistenceValidator).ensureClientExists(clientId);
        verify(cityRepository).existsById(cityId);
    }

    @Test
    @DisplayName("existing client and city should pass")
    void validateClientAndCityExist_bothExist_doesNotThrow() throws ResourceNotFoundException {
        Long clientId = 1L;
        Long cityId = 1L;
        doNothing().when(clientExistenceValidator).ensureClientExists(clientId);
        when(cityRepository.existsById(cityId)).thenReturn(true);

        validator.validateClientAndCityExist(clientId, cityId);

        verify(clientExistenceValidator).ensureClientExists(clientId);
        verify(cityRepository).existsById(cityId);
    }
}
