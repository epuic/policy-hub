package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCancelDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.policy.PolicyMapper;
import com.endava.insurance.insurance_service.application.service.premium.PolicyPremiumCalculator;
import com.endava.insurance.insurance_service.application.validator.policy.PolicyValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyServiceImpl")
@SuppressWarnings("java:S1130")
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PolicyMapper policyMapper;
    @Mock
    private PolicyValidator policyValidator;
    @Mock
    private PolicyPremiumCalculator premiumCalculator;
    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    @Test
    @DisplayName("getById when not found throws ResourceNotFoundException")
    void getById_notFound_throws() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> policyService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Policy not found with id: 999");
    }

    @Test
    @DisplayName("getById when found returns DTO")
    void getById_found_returnsDto() throws ResourceNotFoundException {
        Policy policy = mock(Policy.class);
        PolicyResponseDTO dto = mock(PolicyResponseDTO.class);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyMapper.toResponse(policy)).thenReturn(dto);

        PolicyResponseDTO result = policyService.getById(1L);

        assertThat(result).isEqualTo(dto);
        verify(policyRepository).findById(1L);
        verify(policyMapper).toResponse(policy);
    }

    @Test
    @DisplayName("createDraft validates, loads building, calculates premium, saves and returns DTO")
    void createDraft_validRequest_returnsDto() throws ResourceNotFoundException, ValidationException {
        PolicyCreateDTO request = new PolicyCreateDTO(
                10L, 20L, 1L,
                LocalDate.now(ZoneOffset.UTC).plusDays(1), LocalDate.now(ZoneOffset.UTC).plusDays(30),
                new BigDecimal("100.00"), 1L);
        var building = mock(com.endava.insurance.insurance_service.domain.model.Building.class);
        Policy policy = mock(Policy.class);
        Policy saved = mock(Policy.class);
        PolicyResponseDTO dto = mock(PolicyResponseDTO.class);

        doNothing().when(policyValidator).validateNewPolicy(request);
        when(buildingRepository.findById(20L)).thenReturn(Optional.of(building));
        when(premiumCalculator.calculateFinalPremium(any(), any(), any())).thenReturn(new BigDecimal("120.00"));
        when(policyMapper.toEntity(any(), any(), any())).thenReturn(policy);
        when(policyRepository.save(policy)).thenReturn(saved);
        when(policyMapper.toResponse(saved)).thenReturn(dto);

        PolicyResponseDTO result = policyService.createDraft(request);

        assertThat(result).isEqualTo(dto);
        verify(policyValidator).validateNewPolicy(request);
        verify(buildingRepository).findById(20L);
        verify(premiumCalculator).calculateFinalPremium(request.basePremiumAmount(), building, request.startDate());
        verify(policyRepository).save(policy);
    }

    @Test
    @DisplayName("createDraft when building not found throws ResourceNotFoundException")
    void createDraft_buildingNotFound_throws() throws ValidationException,ResourceNotFoundException{
        PolicyCreateDTO request = new PolicyCreateDTO(
                10L, 20L, 1L,
                LocalDate.now(ZoneOffset.UTC).plusDays(1), LocalDate.now(ZoneOffset.UTC).plusDays(30),
                new BigDecimal("100.00"), 1L);
        doNothing().when(policyValidator).validateNewPolicy(request);
        when(buildingRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.createDraft(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Building not found with id: 20");
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("activate when not found throws ResourceNotFoundException")
    void activate_notFound_throws() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.activate(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Policy not found with id: 999");
    }

    @Test
    @DisplayName("cancel when not found throws ResourceNotFoundException")
    void cancel_notFound_throws() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());
        PolicyCancelDTO cancelDto = new PolicyCancelDTO("Reason");

        assertThatThrownBy(() -> policyService.cancel(999L, cancelDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Policy not found with id: 999");
    }

    @Test
    @DisplayName("activate when found calls policy.activate and saves")
    void activate_found_activatesAndSaves() throws ResourceNotFoundException, ValidationException {
        Policy policy = mock(Policy.class);
        Policy saved = mock(Policy.class);
        PolicyResponseDTO dto = mock(PolicyResponseDTO.class);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        doNothing().when(policy).activate();
        when(policyRepository.save(policy)).thenReturn(saved);
        when(policyMapper.toResponse(saved)).thenReturn(dto);

        PolicyResponseDTO result = policyService.activate(1L);

        assertThat(result).isEqualTo(dto);
        verify(policy).activate();
        verify(policyRepository).save(policy);
    }

    @Test
    @DisplayName("cancel when found calls policy.cancel and saves")
    void cancel_found_cancelsAndSaves() throws ResourceNotFoundException, ValidationException {
        Policy policy = mock(Policy.class);
        Policy saved = mock(Policy.class);
        PolicyResponseDTO dto = mock(PolicyResponseDTO.class);
        PolicyCancelDTO cancelDto = new PolicyCancelDTO("Client request");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        doNothing().when(policy).cancel("Client request");
        when(policyRepository.save(policy)).thenReturn(saved);
        when(policyMapper.toResponse(saved)).thenReturn(dto);

        PolicyResponseDTO result = policyService.cancel(1L, cancelDto);

        assertThat(result).isEqualTo(dto);
        verify(policy).cancel("Client request");
        verify(policyRepository).save(policy);
    }

    @Test
    @DisplayName("getFiltered returns mapped page")
    void getFiltered_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Policy policy = mock(Policy.class);
        Page<Policy> policyPage = new PageImpl<>(List.of(policy), pageable, 1);
        PolicyResponseDTO dto = mock(PolicyResponseDTO.class);

        when(policyRepository.findFiltered(null, null, null, null, null, pageable)).thenReturn(policyPage);
        when(policyMapper.toResponse(policy)).thenReturn(dto);

        Page<PolicyResponseDTO> result = policyService.getFiltered(null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
    }
}
