package com.endava.insurance.insurance_service.application.mapper.building;

import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.application.mapper.policy.PolicyMapper;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(BuildingMapper.class)
@DisplayName("BuildingMapper")
class BuildingMapperTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private PolicyMapper policyMapper;

    @org.springframework.beans.factory.annotation.Autowired
    private BuildingMapper buildingMapper;

    @Test
    @DisplayName("toResponse maps building to DTO with full address and geography")
    void toResponse_mapsBuildingToDto() {
        Country country = mock(Country.class);
        when(country.getName()).thenReturn("Romania");
        County county = mock(County.class);
        when(county.getName()).thenReturn("Bucuresti");
        when(county.getCountry()).thenReturn(country);
        City city = mock(City.class);
        when(city.getName()).thenReturn("Sector 1");
        when(city.getCounty()).thenReturn(county);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(10L);
        when(owner.getName()).thenReturn("Client Name");
        RiskFactor rf = mock(RiskFactor.class);
        when(rf.getType()).thenReturn(RiskFactorType.FLOOD_ZONE);

        Building building = mock(Building.class);
        when(building.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);
        when(building.getStreet()).thenReturn("Str. X");
        when(building.getNumber()).thenReturn("1");
        when(building.getCity()).thenReturn(city);
        when(building.getConstructionYear()).thenReturn(2000);
        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.getNumberOfFloors()).thenReturn(3);
        when(building.getSurfaceArea()).thenReturn(85.5);
        when(building.getInsuredValue()).thenReturn(150000.0);
        when(building.getRiskFactors()).thenReturn(Set.of(rf));

        BuildingResponseDTO dto = buildingMapper.toResponse(building);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.clientId()).isEqualTo(10L);
        assertThat(dto.clientName()).isEqualTo("Client Name");
        assertThat(dto.fullAddress()).isEqualTo("Str. X, Nr. 1, Sector 1");
        assertThat(dto.cityName()).isEqualTo("Sector 1");
        assertThat(dto.countyName()).isEqualTo("Bucuresti");
        assertThat(dto.countryName()).isEqualTo("Romania");
        assertThat(dto.constructionYear()).isEqualTo(2000);
        assertThat(dto.type()).isEqualTo(BuildingType.RESIDENTIAL);
        assertThat(dto.numberOfFloors()).isEqualTo(3);
        assertThat(dto.surfaceArea()).isEqualTo(85.5);
        assertThat(dto.insuredValue()).isEqualTo(150000.0);
        assertThat(dto.riskFactorTypes()).containsExactly(RiskFactorType.FLOOD_ZONE);
    }

    @Test
    @DisplayName("toResponseV2 with null policies returns DTO with empty policy list")
    void toResponseV2_nullPolicies_returnsEmptyPolicyList() {
        Country country = mock(Country.class);
        when(country.getName()).thenReturn("Romania");
        County county = mock(County.class);
        when(county.getName()).thenReturn("Bucuresti");
        when(county.getCountry()).thenReturn(country);
        City city = mock(City.class);
        when(city.getName()).thenReturn("Sector 1");
        when(city.getCounty()).thenReturn(county);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(10L);
        when(owner.getName()).thenReturn("Client Name");

        Building building = mock(Building.class);
        when(building.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);
        when(building.getStreet()).thenReturn("Str. X");
        when(building.getNumber()).thenReturn("1");
        when(building.getCity()).thenReturn(city);
        when(building.getConstructionYear()).thenReturn(2000);
        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.getNumberOfFloors()).thenReturn(2);
        when(building.getSurfaceArea()).thenReturn(80.0);
        when(building.getInsuredValue()).thenReturn(100_000.0);
        when(building.getRiskFactors()).thenReturn(Set.of());

        BuildingResponseDTOV2 result = buildingMapper.toResponseV2(building, null);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.clientId()).isEqualTo(10L);
        assertThat(result.fullAddress()).isEqualTo("Str. X, Nr. 1, Sector 1");
        assertThat(result.policies()).isEmpty();
    }

    @Test
    @DisplayName("toResponseV2 with non-null policies maps to policy summaries")
    void toResponseV2_withPolicies_mapsToSummaries() {
        Country country = mock(Country.class);
        when(country.getName()).thenReturn("Romania");
        County county = mock(County.class);
        when(county.getName()).thenReturn("Bucuresti");
        when(county.getCountry()).thenReturn(country);
        City city = mock(City.class);
        when(city.getName()).thenReturn("Sector 1");
        when(city.getCounty()).thenReturn(county);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(10L);
        when(owner.getName()).thenReturn("Client");

        Building building = mock(Building.class);
        when(building.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);
        when(building.getStreet()).thenReturn("Str");
        when(building.getNumber()).thenReturn("1");
        when(building.getCity()).thenReturn(city);
        when(building.getConstructionYear()).thenReturn(2000);
        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.getNumberOfFloors()).thenReturn(2);
        when(building.getSurfaceArea()).thenReturn(80.0);
        when(building.getInsuredValue()).thenReturn(100_000.0);
        when(building.getRiskFactors()).thenReturn(Set.of());

        Policy policy = mock(Policy.class);
        PolicySummaryDTO summary = new PolicySummaryDTO(
                1L, "POL-1", PolicyStatus.ACTIVE, null, null, null, "EUR", null);
        when(policyMapper.toSummary(policy)).thenReturn(summary);

        BuildingResponseDTOV2 result = buildingMapper.toResponseV2(building, List.of(policy));

        assertThat(result.policies()).hasSize(1);
        assertThat(result.policies().get(0)).isEqualTo(summary);
        verify(policyMapper).toSummary(policy);
    }
}