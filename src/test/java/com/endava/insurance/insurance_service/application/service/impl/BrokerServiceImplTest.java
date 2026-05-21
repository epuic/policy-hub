package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.mapper.broker.BrokerMapper;
import com.endava.insurance.insurance_service.application.validator.broker.BrokerValidator;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import com.endava.insurance.insurance_service.domain.model.auth.BrokerAuth;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrokerServiceImpl")
class BrokerServiceImplTest {

    @Mock
    private BrokerRepository brokerRepository;
    @Mock
    private com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository brokerAuthRepository;
    @Mock
    private BrokerMapper brokerMapper;
    @Mock
    private BrokerValidator brokerValidator;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private BrokerServiceImpl brokerService;

    @Test
    @DisplayName("create validates, maps, saves and returns DTO")
    void create_validRequest_returnsDto() throws ValidationException {
        BrokerCreateDTO request = new BrokerCreateDTO("BRK1", "Broker One", "b@b.com", "+40111", "password123", BrokerStatus.ACTIVE, BigDecimal.TEN);
        Broker entity = mock(Broker.class);
        Broker saved = mock(Broker.class);
        BrokerResponseDTO dto = new BrokerResponseDTO(1L, "BRK1", "Broker One", "b@b.com", "+40111", BrokerStatus.ACTIVE, BigDecimal.TEN);

        doNothing().when(brokerValidator).validateNewBroker(request);
        when(brokerMapper.toEntity(any())).thenReturn(entity);
        when(brokerRepository.save(entity)).thenReturn(saved);
        when(brokerMapper.toResponse(saved)).thenReturn(dto);

        BrokerResponseDTO result = brokerService.create(request);

        assertThat(result).isEqualTo(dto);
        verify(brokerValidator).validateNewBroker(request);
        verify(brokerRepository).save(entity);
    }

    @Test
    @DisplayName("getById when not found throws ResourceNotFoundException")
    void getById_notFound_throws() {
        when(brokerRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> brokerService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker not found with id: 999");
    }

    @Test
    @DisplayName("update when found validates, updates and returns DTO")
    void update_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        Long id = 1L;
        Broker broker = mock(Broker.class);
        BrokerUpdateDTO update = new BrokerUpdateDTO("New Name", "new@b.com", "+40222", BigDecimal.ONE);
        Broker saved = mock(Broker.class);
        BrokerAuth auth = mock(BrokerAuth.class);
        BrokerResponseDTO dto = new BrokerResponseDTO(id, "BRK1", "New Name", "new@b.com", "+40222", BrokerStatus.ACTIVE, BigDecimal.ONE);

        when(brokerRepository.findById(id)).thenReturn(Optional.of(broker));
        doNothing().when(brokerValidator).validateBrokerUpdate(broker, update);
        doNothing().when(brokerMapper).updateEntityFromRequest(update, broker);
        when(brokerRepository.save(broker)).thenReturn(saved);
        when(brokerAuthRepository.findByBrokerId(id)).thenReturn(Optional.of(auth));
        when(auth.getEmail()).thenReturn("old@b.com");
        when(brokerMapper.toResponse(saved)).thenReturn(dto);

        BrokerResponseDTO result = brokerService.update(id, update);

        assertThat(result).isEqualTo(dto);
        verify(brokerMapper).updateEntityFromRequest(update, broker);
        verify(auth).updateEmail("new@b.com");
        verify(brokerAuthRepository).save(auth);
    }

    @Test
    @DisplayName("getAll returns mapped page")
    void getAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Broker broker = mock(Broker.class);
        Page<Broker> brokerPage = new PageImpl<>(List.of(broker), pageable, 1);
        BrokerResponseDTO dto = new BrokerResponseDTO(1L, "BRK1", "Broker", "b@b.com", "+40123456789", BrokerStatus.ACTIVE, BigDecimal.ONE);

        when(brokerRepository.findAll(pageable)).thenReturn(brokerPage);
        when(brokerMapper.toResponse(broker)).thenReturn(dto);

        Page<BrokerResponseDTO> result = brokerService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getById when found returns DTO")
    void getById_found_returnsDto() throws ResourceNotFoundException {
        Broker broker = mock(Broker.class);
        BrokerResponseDTO dto = new BrokerResponseDTO(1L, "BRK1", "Broker One", "b@b.com", "+40123456789", BrokerStatus.ACTIVE, BigDecimal.TEN);
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));
        when(brokerMapper.toResponse(broker)).thenReturn(dto);

        BrokerResponseDTO result = brokerService.getById(1L);

        assertThat(result).isEqualTo(dto);
        verify(brokerRepository).findById(1L);
        verify(brokerMapper).toResponse(broker);
    }

    @Test
    @DisplayName("update when not found throws ResourceNotFoundException")
    void update_notFound_throws() throws ValidationException {
        when(brokerRepository.findById(999L)).thenReturn(Optional.empty());
        BrokerUpdateDTO update = new BrokerUpdateDTO("New", "n@b.com", "+40223456789", BigDecimal.ONE);

        assertThatThrownBy(() -> brokerService.update(999L, update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker not found with id: 999");
        verify(brokerValidator, never()).validateBrokerUpdate(any(), any());
    }

    @Test
    @DisplayName("activate when found calls broker.activate and returns DTO")
    void activate_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        Broker broker = mock(Broker.class);
        Broker saved = mock(Broker.class);
        BrokerResponseDTO dto = new BrokerResponseDTO(1L, "BRK1", "Broker", "b@b.com", "+40123456789", BrokerStatus.ACTIVE, BigDecimal.ONE);
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));
        doNothing().when(broker).activate();
        when(brokerRepository.save(broker)).thenReturn(saved);
        when(brokerMapper.toResponse(saved)).thenReturn(dto);

        BrokerResponseDTO result = brokerService.activate(1L);

        assertThat(result).isEqualTo(dto);
        verify(broker).activate();
        verify(brokerRepository).save(broker);
    }

    @Test
    @DisplayName("activate when not found throws ResourceNotFoundException")
    void activate_notFound_throws() {
        when(brokerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brokerService.activate(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker not found with id: 999");
    }

    @Test
    @DisplayName("deactivate when found calls broker.deactivate and returns DTO")
    void deactivate_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        Broker broker = mock(Broker.class);
        Broker saved = mock(Broker.class);
        BrokerResponseDTO dto = new BrokerResponseDTO(1L, "BRK1", "Broker", "b@b.com", "+40123456789", BrokerStatus.INACTIVE, BigDecimal.ONE);
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));
        doNothing().when(broker).deactivate();
        when(brokerRepository.save(broker)).thenReturn(saved);
        when(brokerMapper.toResponse(saved)).thenReturn(dto);

        BrokerResponseDTO result = brokerService.deactivate(1L);

        assertThat(result).isEqualTo(dto);
        verify(broker).deactivate();
        verify(brokerRepository).save(broker);
    }

    @Test
    @DisplayName("deactivate when not found throws ResourceNotFoundException")
    void deactivate_notFound_throws() {
        when(brokerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brokerService.deactivate(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker not found with id: 999");
    }
}
