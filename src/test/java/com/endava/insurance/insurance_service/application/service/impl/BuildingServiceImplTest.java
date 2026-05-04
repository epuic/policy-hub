package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import com.endava.insurance.insurance_service.application.validator.building.BuildingValidator;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingServiceImpl - buildings management")
@SuppressWarnings("java:S1130")
class BuildingServiceImplTest {

    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private BuildingMapper buildingMapper;
    @Mock
    private BuildingValidator buildingValidator;

    @InjectMocks
    private BuildingServiceImpl buildingService;

    @Test
    @DisplayName("createBuilding validates client and city, maps, saves and returns DTO")
    void createBuilding_validRequest_returnsSavedBuildingDto() throws ResourceNotFoundException, ValidationException {
        Long clientId = 1L;
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Strada X", "1", 1L, 2000, BuildingType.RESIDENTIAL,
                2, 80.0, 100_000.0, List.of()
        );
        Building entity = mock(Building.class);
        Building saved = mock(Building.class);
        BuildingResponseDTO dto = new BuildingResponseDTO(
                1L, clientId, "Client", "Strada X, Nr. 1, City",
                "City", "County", "Country", 2000, BuildingType.RESIDENTIAL,
                2, 80.0, 100_000.0, List.of()
        );
        doNothing().when(buildingValidator).validateClientAndCityExist(clientId, 1L);
        when(buildingMapper.toEntity(request, clientId)).thenReturn(entity);
        when(buildingRepository.save(any(Building.class))).thenReturn(saved);
        when(buildingMapper.toResponse(saved)).thenReturn(dto);

        BuildingResponseDTO result = buildingService.createBuilding(clientId, request);

        assertThat(result).isEqualTo(dto);
        verify(buildingValidator).validateClientAndCityExist(clientId, 1L);
        verify(buildingMapper).toEntity(request, clientId);
        verify(buildingRepository).save(any(Building.class));
        verify(buildingMapper).toResponse(saved);
    }

    @Test
    @DisplayName("getBuildingById when not found throws ResourceNotFoundException")
    void getBuildingById_notFound_throws() throws ResourceNotFoundException {
        when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.getBuildingById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Building not found");
        verify(buildingMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("getBuildingById when found returns DTO")
    void getBuildingById_found_returnsDto() throws ResourceNotFoundException {
        Building building = mock(Building.class);
        BuildingResponseDTO dto = new BuildingResponseDTO(
                1L, 10L, "Client", "Addr", "City", "County", "Country", 2000,
                BuildingType.RESIDENTIAL, 2, 80.0, 100_000.0, List.of());
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(buildingMapper.toResponse(building)).thenReturn(dto);

        BuildingResponseDTO result = buildingService.getBuildingById(1L);

        assertThat(result).isEqualTo(dto);
        verify(buildingRepository).findById(1L);
        verify(buildingMapper).toResponse(building);
    }

    @Test
    @DisplayName("getBuildingByIdV2 when found returns DTO with policies")
    void getBuildingByIdV2_found_returnsDto() throws ResourceNotFoundException {
        Building building = mock(Building.class);
        BuildingResponseDTOV2 dtoV2 = mock(BuildingResponseDTOV2.class);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(policyRepository.findByBuildingIdOrderByStartDateDesc(1L)).thenReturn(List.of());
        when(buildingMapper.toResponseV2(building, List.of())).thenReturn(dtoV2);

        BuildingResponseDTOV2 result = buildingService.getBuildingByIdV2(1L);

        assertThat(result).isEqualTo(dtoV2);
        verify(buildingRepository).findById(1L);
        verify(policyRepository).findByBuildingIdOrderByStartDateDesc(1L);
    }

    @Test
    @DisplayName("getBuildingByIdV2 when not found throws ResourceNotFoundException")
    void getBuildingByIdV2_notFound_throws() {
        when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.getBuildingByIdV2(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Building not found with id: 999");
        verify(policyRepository, never()).findByBuildingIdOrderByStartDateDesc(any());
    }

    @Test
    @DisplayName("getBuildingsByClientId returns mapped page")
    void getBuildingsByClientId_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Building building = mock(Building.class);
        Page<Building> page = new PageImpl<>(List.of(building), pageable, 1);
        BuildingResponseDTO dto = new BuildingResponseDTO(
                1L, 10L, "Client", "Addr", "City", "County", "Country", 2000,
                BuildingType.RESIDENTIAL, 2, 80.0, 100_000.0, List.of());
        when(buildingRepository.findByOwnerId(10L, pageable)).thenReturn(page);
        when(buildingMapper.toResponse(building)).thenReturn(dto);

        Page<BuildingResponseDTO> result = buildingService.getBuildingsByClientId(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
        verify(buildingRepository).findByOwnerId(10L, pageable);
    }

    @Test
    @DisplayName("getBuildingsByClientIdV2 returns page of V2 DTOs")
    void getBuildingsByClientIdV2_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Building building = mock(Building.class);
        when(building.getId()).thenReturn(1L);
        Page<Building> page = new PageImpl<>(List.of(building), pageable, 1);
        BuildingResponseDTOV2 dtoV2 = mock(BuildingResponseDTOV2.class);
        when(buildingRepository.findByOwnerId(10L, pageable)).thenReturn(page);
        when(policyRepository.findByBuildingIdOrderByStartDateDesc(1L)).thenReturn(List.of());
        when(buildingMapper.toResponseV2(building, List.of())).thenReturn(dtoV2);

        Page<BuildingResponseDTOV2> result = buildingService.getBuildingsByClientIdV2(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dtoV2);
        verify(buildingRepository).findByOwnerId(10L, pageable);
    }

    @Test
    @DisplayName("updateBuilding when found validates, updates and returns DTO")
    void updateBuilding_found_returnsDto() throws ResourceNotFoundException, ValidationException {
        Building building = mock(Building.class);
        var owner = mock(com.endava.insurance.insurance_service.domain.model.Client.class);
        when(owner.getId()).thenReturn(10L);
        when(building.getOwner()).thenReturn(owner);
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Str Y", "2", 1L, 2001, BuildingType.INDUSTRIAL, 2, 100.0, 200_000.0, List.of());
        BuildingResponseDTO dto = new BuildingResponseDTO(
                1L, 10L, "Client", "Str Y, Nr. 2, City", "City", "County", "Country", 2001,
                BuildingType.INDUSTRIAL, 2, 100.0, 200_000.0, List.of());
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        doNothing().when(buildingValidator).validateClientAndCityExist(10L, 1L);
        doNothing().when(buildingMapper).updateEntityFromRequest(request, building);
        when(buildingRepository.save(building)).thenReturn(building);
        when(buildingMapper.toResponse(building)).thenReturn(dto);

        BuildingResponseDTO result = buildingService.updateBuilding(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(buildingValidator).validateClientAndCityExist(10L, 1L);
        verify(buildingMapper).updateEntityFromRequest(request, building);
        verify(buildingRepository).save(building);
    }

    @Test
    @DisplayName("updateBuilding when not found throws ResourceNotFoundException")
    void updateBuilding_notFound_throws() throws ResourceNotFoundException{
        when(buildingRepository.findById(999L)).thenReturn(Optional.empty());
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Str", "1", 1L, 2000, BuildingType.RESIDENTIAL, 2, 80.0, 100_000.0, List.of());

        assertThatThrownBy(() -> buildingService.updateBuilding(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Building not found with id: 999");
        verify(buildingValidator, never()).validateClientAndCityExist(any(), any());
    }
}
