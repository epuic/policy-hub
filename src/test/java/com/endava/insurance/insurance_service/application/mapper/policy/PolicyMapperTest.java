package com.endava.insurance.insurance_service.application.mapper.policy;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.Broker;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyMapper")
@SuppressWarnings("java:S1130")
class PolicyMapperTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PolicyMapper mapper;

    @Test
    @DisplayName("toEntity builds Policy with references from request")
    void toEntity_buildsPolicyWithParties() throws Exception {
        PolicyCreateDTO request = new PolicyCreateDTO(1L, 2L, 3L,
                LocalDate.now(ZoneOffset.UTC).plusDays(1), LocalDate.now(ZoneOffset.UTC).plusDays(365),
                new BigDecimal("100"), 1L);
        Client client = mock(Client.class);
        Building building = mock(Building.class);
        Broker broker = mock(Broker.class);
        Currency currency = mock(Currency.class);

        when(entityManager.getReference(Client.class, 1L)).thenReturn(client);
        when(entityManager.getReference(Building.class, 2L)).thenReturn(building);
        when(entityManager.getReference(Broker.class, 3L)).thenReturn(broker);
        when(entityManager.getReference(Currency.class, 1L)).thenReturn(currency);

        Policy policy = mapper.toEntity(request, "POL-123", new BigDecimal("105"));

        assertThat(policy.getPolicyNumber()).isEqualTo("POL-123");
        assertThat(policy.getClient()).isSameAs(client);
        assertThat(policy.getBuilding()).isSameAs(building);
        assertThat(policy.getBroker()).isSameAs(broker);
        assertThat(policy.getStartDate()).isEqualTo(request.startDate());
        assertThat(policy.getEndDate()).isEqualTo(request.endDate());
        assertThat(policy.getBasePremiumAmount()).isEqualByComparingTo(request.basePremiumAmount());
        assertThat(policy.getCurrency()).isSameAs(currency);
        assertThat(policy.getFinalPremium()).isEqualByComparingTo("105");
        assertThat(policy.getStatus()).isEqualTo(PolicyStatus.DRAFT);
    }

    @Test
    @DisplayName("toResponse maps policy to full response DTO")
    void toResponse_mapsToResponseDto() {
        Client client = mock(Client.class);
        Building building = mock(Building.class);
        Broker broker = mock(Broker.class);
        City city = mock(City.class);
        County county = mock(County.class);
        Country country = mock(Country.class);
        Currency currency = mock(Currency.class);

        when(client.getId()).thenReturn(1L);
        when(client.getName()).thenReturn("Client Name");
        when(building.getId()).thenReturn(10L);
        when(building.getStreet()).thenReturn("Str. Example");
        when(building.getNumber()).thenReturn("5");
        when(building.getCity()).thenReturn(city);
        when(city.getName()).thenReturn("Bucharest");
        when(city.getCounty()).thenReturn(county);
        when(county.getName()).thenReturn("Bucuresti");
        when(county.getCountry()).thenReturn(country);
        when(country.getName()).thenReturn("Romania");
        when(broker.getId()).thenReturn(2L);
        when(broker.getName()).thenReturn("Broker Name");
        when(currency.getCode()).thenReturn("EUR");

        Policy policy = mock(Policy.class);
        when(policy.getId()).thenReturn(100L);
        when(policy.getPolicyNumber()).thenReturn("POL-ABC");
        when(policy.getClient()).thenReturn(client);
        when(policy.getBuilding()).thenReturn(building);
        when(policy.getBroker()).thenReturn(broker);
        when(policy.getStatus()).thenReturn(PolicyStatus.ACTIVE);
        when(policy.getStartDate()).thenReturn(LocalDate.of(2026, 1, 1));
        when(policy.getEndDate()).thenReturn(LocalDate.of(2027, 1, 1));
        when(policy.getBasePremiumAmount()).thenReturn(new BigDecimal("100"));
        when(policy.getCurrency()).thenReturn(currency);
        when(policy.getFinalPremium()).thenReturn(new BigDecimal("105"));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        when(policy.getCreatedAt()).thenReturn(now);
        when(policy.getLastUpdatedAt()).thenReturn(now);
        when(policy.getCancellationDate()).thenReturn(null);
        when(policy.getCancellationReason()).thenReturn(null);

        PolicyResponseDTO dto = mapper.toResponse(policy);

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.policyNumber()).isEqualTo("POL-ABC");
        assertThat(dto.clientId()).isEqualTo(1L);
        assertThat(dto.clientName()).isEqualTo("Client Name");
        assertThat(dto.buildingId()).isEqualTo(10L);
        assertThat(dto.buildingAddress()).isEqualTo("Str. Example, Nr. 5, Bucharest");
        assertThat(dto.cityName()).isEqualTo("Bucharest");
        assertThat(dto.countyName()).isEqualTo("Bucuresti");
        assertThat(dto.countryName()).isEqualTo("Romania");
        assertThat(dto.brokerId()).isEqualTo(2L);
        assertThat(dto.brokerName()).isEqualTo("Broker Name");
        assertThat(dto.status()).isEqualTo(PolicyStatus.ACTIVE);
        assertThat(dto.currencyCode()).isEqualTo("EUR");
        assertThat(dto.finalPremium()).isEqualByComparingTo("105");
    }

    @Test
    @DisplayName("toSummary maps policy to summary DTO")
    void toSummary_mapsToSummaryDto() {
        Currency currency = mock(Currency.class);
        when(currency.getCode()).thenReturn("RON");

        Policy policy = mock(Policy.class);
        when(policy.getId()).thenReturn(100L);
        when(policy.getPolicyNumber()).thenReturn("POL-X");
        when(policy.getStatus()).thenReturn(PolicyStatus.DRAFT);
        when(policy.getStartDate()).thenReturn(LocalDate.of(2026, 2, 1));
        when(policy.getEndDate()).thenReturn(LocalDate.of(2027, 2, 1));
        when(policy.getFinalPremium()).thenReturn(new BigDecimal("110"));
        when(policy.getCurrency()).thenReturn(currency);
        when(policy.getCreatedAt()).thenReturn(LocalDateTime.now(ZoneOffset.UTC));

        PolicySummaryDTO dto = mapper.toSummary(policy);

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.policyNumber()).isEqualTo("POL-X");
        assertThat(dto.status()).isEqualTo(PolicyStatus.DRAFT);
        assertThat(dto.finalPremium()).isEqualByComparingTo("110");
        assertThat(dto.currencyCode()).isEqualTo("RON");
    }
}
