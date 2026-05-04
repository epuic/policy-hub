package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.RiskFactorConfigurationMapper;
import com.endava.insurance.insurance_service.application.validator.metadata.RiskFactorConfigurationValidator;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskFactorConfigurationServiceImpl")
class RiskFactorConfigurationServiceImplTest {

    @Mock
    private RiskFactorConfigurationRepository riskFactorConfigurationRepository;
    @Mock
    private RiskFactorConfigurationMapper riskFactorConfigurationMapper;
    @Mock
    private RiskFactorConfigurationValidator riskFactorConfigurationValidator;

    @InjectMocks
    private RiskFactorConfigurationServiceImpl riskFactorConfigurationService;

    @Test
    @DisplayName("create validates, maps, saves and returns DTO")
    void create_validRequest_returnsDto() throws ValidationException {
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.CITY, "1", new BigDecimal("5"), true);
        RiskFactorConfiguration entity = mock(RiskFactorConfiguration.class);
        RiskFactorConfiguration saved = mock(RiskFactorConfiguration.class);
        RiskFactorConfigurationResponseDTO dto = new RiskFactorConfigurationResponseDTO(1L, RiskFactorConfigLevel.CITY, "1", "Bucuresti", new BigDecimal("5"), true);

        doNothing().when(riskFactorConfigurationValidator).validateNewRiskFactorConfiguration(request);
        when(riskFactorConfigurationMapper.toEntity(request)).thenReturn(entity);
        when(riskFactorConfigurationRepository.save(entity)).thenReturn(saved);
        when(riskFactorConfigurationMapper.toResponse(saved)).thenReturn(dto);

        RiskFactorConfigurationResponseDTO result = riskFactorConfigurationService.create(request);

        assertThat(result).isEqualTo(dto);
        verify(riskFactorConfigurationValidator).validateNewRiskFactorConfiguration(request);
    }

    @Test
    @DisplayName("getById when not found throws ResourceNotFoundException")
    void getById_notFound_throws() {
        when(riskFactorConfigurationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> riskFactorConfigurationService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Risk factor configuration not found with id: 999");
    }

    @Test
    @DisplayName("getAll returns mapped page")
    void getAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskFactorConfiguration config = mock(RiskFactorConfiguration.class);
        Page<RiskFactorConfiguration> page = new PageImpl<>(List.of(config), pageable, 1);
        RiskFactorConfigurationResponseDTO dto = new RiskFactorConfigurationResponseDTO(1L, RiskFactorConfigLevel.CITY, "1", "City", new BigDecimal("5"), true);

        when(riskFactorConfigurationRepository.findAll(pageable)).thenReturn(page);
        when(riskFactorConfigurationMapper.toResponse(config)).thenReturn(dto);

        Page<RiskFactorConfigurationResponseDTO> result = riskFactorConfigurationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getById when found returns DTO")
    void getById_found_returnsDto() throws ResourceNotFoundException {
        RiskFactorConfiguration config = mock(RiskFactorConfiguration.class);
        RiskFactorConfigurationResponseDTO dto = new RiskFactorConfigurationResponseDTO(1L, RiskFactorConfigLevel.COUNTRY, "1", "Romania", new BigDecimal("10"), true);
        when(riskFactorConfigurationRepository.findById(1L)).thenReturn(Optional.of(config));
        when(riskFactorConfigurationMapper.toResponse(config)).thenReturn(dto);

        RiskFactorConfigurationResponseDTO result = riskFactorConfigurationService.getById(1L);

        assertThat(result).isEqualTo(dto);
        verify(riskFactorConfigurationRepository).findById(1L);
    }

    @Test
    @DisplayName("update when found updates and returns DTO")
    void update_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        RiskFactorConfiguration config = mock(RiskFactorConfiguration.class);
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.CITY, "2", new BigDecimal("15"), false);
        RiskFactorConfigurationResponseDTO dto = new RiskFactorConfigurationResponseDTO(1L, RiskFactorConfigLevel.CITY, "2", "Bucuresti", new BigDecimal("15"), false);
        when(riskFactorConfigurationRepository.findById(1L)).thenReturn(Optional.of(config));
        doNothing().when(riskFactorConfigurationValidator).validateRiskFactorConfigurationUpdate(request);
        doNothing().when(riskFactorConfigurationMapper).updateEntityFromRequest(request, config);
        when(riskFactorConfigurationRepository.save(config)).thenReturn(config);
        when(riskFactorConfigurationMapper.toResponse(config)).thenReturn(dto);

        RiskFactorConfigurationResponseDTO result = riskFactorConfigurationService.update(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(riskFactorConfigurationValidator).validateRiskFactorConfigurationUpdate(request);
        verify(riskFactorConfigurationMapper).updateEntityFromRequest(request, config);
    }

    @Test
    @DisplayName("update when not found throws ResourceNotFoundException")
    void update_notFound_throws() throws ValidationException {
        when(riskFactorConfigurationRepository.findById(999L)).thenReturn(Optional.empty());
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.CITY, "1", new BigDecimal("5"), true);

        assertThatThrownBy(() -> riskFactorConfigurationService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Risk factor configuration not found with id: 999");
        verify(riskFactorConfigurationValidator).validateRiskFactorConfigurationUpdate(request);
        verify(riskFactorConfigurationMapper, never()).updateEntityFromRequest(any(), any());
    }
}
