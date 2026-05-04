package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.metadata.CurrencyMapper;
import com.endava.insurance.insurance_service.application.validator.metadata.CurrencyValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
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
@DisplayName("CurrencyServiceImpl")
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CurrencyMapper currencyMapper;
    @Mock
    private CurrencyValidator currencyValidator;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    @DisplayName("create validates, maps, saves and returns DTO")
    void create_validRequest_returnsDto() throws ValidationException {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", BigDecimal.ONE, true);
        Currency entity = mock(Currency.class);
        Currency saved = mock(Currency.class);
        CurrencyResponseDTO dto = new CurrencyResponseDTO(1L, "EUR", "Euro", BigDecimal.ONE, true);

        doNothing().when(currencyValidator).validateNewCurrency(request);
        when(currencyMapper.toEntity(request)).thenReturn(entity);
        when(currencyRepository.save(entity)).thenReturn(saved);
        when(currencyMapper.toResponse(saved)).thenReturn(dto);

        CurrencyResponseDTO result = currencyService.create(request);

        assertThat(result).isEqualTo(dto);
        verify(currencyValidator).validateNewCurrency(request);
    }

    @Test
    @DisplayName("getById when not found throws ResourceNotFoundException")
    void getById_notFound_throws() {
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> currencyService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
    }

    @Test
    @DisplayName("getAll returns mapped page")
    void getAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Currency currency = mock(Currency.class);
        Page<Currency> page = new PageImpl<>(List.of(currency), pageable, 1);
        CurrencyResponseDTO dto = new CurrencyResponseDTO(1L, "RON", "Leu", BigDecimal.ONE, true);

        when(currencyRepository.findAll(pageable)).thenReturn(page);
        when(currencyMapper.toResponse(currency)).thenReturn(dto);

        Page<CurrencyResponseDTO> result = currencyService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getById when found returns DTO")
    void getById_found_returnsDto() throws ResourceNotFoundException {
        Currency currency = mock(Currency.class);
        CurrencyResponseDTO dto = new CurrencyResponseDTO(1L, "EUR", "Euro", BigDecimal.ONE, true);
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyMapper.toResponse(currency)).thenReturn(dto);

        CurrencyResponseDTO result = currencyService.getById(1L);

        assertThat(result).isEqualTo(dto);
        verify(currencyRepository).findById(1L);
        verify(currencyMapper).toResponse(currency);
    }

    @Test
    @DisplayName("update when found validates, updates and returns DTO")
    void update_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        Currency currency = mock(Currency.class);
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro Updated", new BigDecimal("1.1"), true);
        CurrencyResponseDTO dto = new CurrencyResponseDTO(1L, "EUR", "Euro Updated", new BigDecimal("1.1"), true);

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        doNothing().when(currencyValidator).validateCurrencyUpdate(currency, request);
        doNothing().when(currencyMapper).updateEntityFromRequest(request, currency);
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(currencyMapper.toResponse(currency)).thenReturn(dto);

        CurrencyResponseDTO result = currencyService.update(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(currencyValidator).validateCurrencyUpdate(currency, request);
        verify(currencyMapper).updateEntityFromRequest(request, currency);
    }

    @Test
    @DisplayName("update when not found throws ResourceNotFoundException")
    void update_notFound_throws() throws ValidationException {
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", BigDecimal.ONE, true);

        assertThatThrownBy(() -> currencyService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyValidator, never()).validateCurrencyUpdate(any(), any());
    }

    @Test
    @DisplayName("update when deactivating validates deactivation")
    void update_deactivating_validatesDeactivation() throws ResourceNotFoundException, ValidationException {
        Currency currency = mock(Currency.class);
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", BigDecimal.ONE, false);

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        doNothing().when(currencyValidator).validateCurrencyUpdate(currency, request);
        when(currency.isActive()).thenReturn(true);
        doNothing().when(currencyValidator).validateCurrencyDeactivation(currency);
        doNothing().when(currencyMapper).updateEntityFromRequest(request, currency);
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(currencyMapper.toResponse(currency)).thenReturn(new CurrencyResponseDTO(1L, "EUR", "Euro", BigDecimal.ONE, false));

        currencyService.update(1L, request);

        verify(currencyValidator).validateCurrencyDeactivation(currency);
    }
}
