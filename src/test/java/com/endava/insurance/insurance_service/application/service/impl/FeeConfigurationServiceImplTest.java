package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.FeeConfigurationMapper;
import com.endava.insurance.insurance_service.application.validator.metadata.FeeConfigurationValidator;
import com.endava.insurance.insurance_service.domain.enums.FeeType;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.FeeConfigurationRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeeConfigurationServiceImpl")
class FeeConfigurationServiceImplTest {

    @Mock
    private FeeConfigurationRepository feeConfigurationRepository;
    @Mock
    private FeeConfigurationMapper feeConfigurationMapper;
    @Mock
    private FeeConfigurationValidator feeConfigurationValidator;

    @InjectMocks
    private FeeConfigurationServiceImpl feeConfigurationService;

    @Test
    @DisplayName("create validates, maps, saves and returns DTO")
    void create_validRequest_returnsDto() throws ValidationException {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee", FeeType.BROKER_COMMISSION, new BigDecimal("5"),
                LocalDate.of(2025, 1, 1), null, true);
        FeeConfiguration entity = mock(FeeConfiguration.class);
        FeeConfiguration saved = mock(FeeConfiguration.class);
        FeeConfigurationResponseDTO dto = new FeeConfigurationResponseDTO(1L, "Fee", FeeType.BROKER_COMMISSION, new BigDecimal("5"), LocalDate.of(2025, 1, 1), null, true);

        doNothing().when(feeConfigurationValidator).validateNewFeeConfiguration(request);
        when(feeConfigurationMapper.toEntity(request)).thenReturn(entity);
        when(feeConfigurationRepository.save(entity)).thenReturn(saved);
        when(feeConfigurationMapper.toResponse(saved)).thenReturn(dto);

        FeeConfigurationResponseDTO result = feeConfigurationService.create(request);

        assertThat(result).isEqualTo(dto);
        verify(feeConfigurationValidator).validateNewFeeConfiguration(request);
    }

    @Test
    @DisplayName("getById when not found throws ResourceNotFoundException")
    void getById_notFound_throws() {
        when(feeConfigurationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> feeConfigurationService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fee configuration not found with id: 999");
    }

    @Test
    @DisplayName("getAll returns mapped page")
    void getAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        FeeConfiguration config = mock(FeeConfiguration.class);
        Page<FeeConfiguration> page = new PageImpl<>(List.of(config), pageable, 1);
        FeeConfigurationResponseDTO dto = new FeeConfigurationResponseDTO(1L, "Fee", FeeType.BROKER_COMMISSION, new BigDecimal("5"), null, null, true);

        when(feeConfigurationRepository.findAll(pageable)).thenReturn(page);
        when(feeConfigurationMapper.toResponse(config)).thenReturn(dto);

        Page<FeeConfigurationResponseDTO> result = feeConfigurationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getById when found returns DTO")
    void getById_found_returnsDto() throws ResourceNotFoundException {
        FeeConfiguration config = mock(FeeConfiguration.class);
        FeeConfigurationResponseDTO dto = new FeeConfigurationResponseDTO(1L, "Fee", FeeType.ADMIN_FEE, new BigDecimal("5"), null, null, true);
        when(feeConfigurationRepository.findById(1L)).thenReturn(Optional.of(config));
        when(feeConfigurationMapper.toResponse(config)).thenReturn(dto);

        FeeConfigurationResponseDTO result = feeConfigurationService.getById(1L);

        assertThat(result).isEqualTo(dto);
        verify(feeConfigurationRepository).findById(1L);
    }

    @Test
    @DisplayName("update when found validates, updates and returns DTO")
    void update_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        FeeConfiguration config = mock(FeeConfiguration.class);
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Updated Fee", FeeType.BROKER_COMMISSION, new BigDecimal("8"),
                LocalDate.of(2025, 2, 1), null, true);
        FeeConfigurationResponseDTO dto = new FeeConfigurationResponseDTO(1L, "Updated Fee", FeeType.BROKER_COMMISSION, new BigDecimal("8"), LocalDate.of(2025, 2, 1), null, true);
        when(feeConfigurationRepository.findById(1L)).thenReturn(Optional.of(config));
        doNothing().when(feeConfigurationValidator).validateFeeConfigurationUpdate(request);
        doNothing().when(feeConfigurationMapper).updateEntityFromRequest(request, config);
        when(feeConfigurationRepository.save(config)).thenReturn(config);
        when(feeConfigurationMapper.toResponse(config)).thenReturn(dto);

        FeeConfigurationResponseDTO result = feeConfigurationService.update(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(feeConfigurationValidator).validateFeeConfigurationUpdate(request);
        verify(feeConfigurationMapper).updateEntityFromRequest(request, config);
    }

    @Test
    @DisplayName("update when not found throws ResourceNotFoundException")
    void update_notFound_throws() throws ValidationException{
        when(feeConfigurationRepository.findById(999L)).thenReturn(Optional.empty());
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee", FeeType.ADMIN_FEE, new BigDecimal("5"), null, null, true);

        assertThatThrownBy(() -> feeConfigurationService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fee configuration not found with id: 999");
        verify(feeConfigurationValidator, never()).validateFeeConfigurationUpdate(any());
    }
}
