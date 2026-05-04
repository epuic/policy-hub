package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByBrokerDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCityDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountyDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountryDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.application.validator.report.PolicyReportValidator;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.projection.*;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyReportServiceImpl")
class PolicyReportServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyReportValidator reportValidator;

    @InjectMocks
    private PolicyReportServiceImpl policyReportService;

    private static final PolicyReportFilter VALID_FILTER = new PolicyReportFilter(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 12, 31),
            PolicyStatus.ACTIVE,
            "RON",
            BuildingType.RESIDENTIAL
    );

    @Nested
    @DisplayName("getReportByCountry")
    class GetReportByCountry {

        @Test
        @DisplayName("valid filter – returns mapped DTOs")
        void validFilter_returnsMappedDtos() throws ValidationException {
            PolicyReportByCountryProjection projection = mock(PolicyReportByCountryProjection.class);
            when(projection.getCountryName()).thenReturn("Romania");
            when(projection.getCurrencyCode()).thenReturn("RON");
            when(projection.getPolicyCount()).thenReturn(5L);
            when(projection.getTotalPremium()).thenReturn(new BigDecimal("500.00"));
            when(projection.getTotalInBase()).thenReturn(new BigDecimal("500.00"));

            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByCountry(any(), any(), any(), any(), any()))
                    .thenReturn(List.of(projection));

            List<PolicyReportByCountryDTO> result = policyReportService.getReportByCountry(VALID_FILTER);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).countryName()).isEqualTo("Romania");
            assertThat(result.get(0).currencyCode()).isEqualTo("RON");
            assertThat(result.get(0).policyCount()).isEqualTo(5);
            assertThat(result.get(0).totalFinalPremium()).isEqualByComparingTo("500.00");
            assertThat(result.get(0).totalFinalPremiumInBaseCurrency()).isEqualByComparingTo("500.00");
            verify(reportValidator).validate(VALID_FILTER);
        }

        @Test
        @DisplayName("validation throws – propagates ValidationException")
        void validationThrows_propagates() throws  ValidationException{
            doThrow(new ValidationException("Date range invalid")).when(reportValidator).validate(any());

            assertThatThrownBy(() -> policyReportService.getReportByCountry(VALID_FILTER))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Date range invalid");

            verify(policyRepository, never()).findReportByCountry(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("null policyCount – maps to 0")
        void nullPolicyCount_mapsToZero() throws ValidationException {
            PolicyReportByCountryProjection projection = mock(PolicyReportByCountryProjection.class);
            when(projection.getCountryName()).thenReturn("Romania");
            when(projection.getCurrencyCode()).thenReturn("EUR");
            when(projection.getPolicyCount()).thenReturn(null);
            when(projection.getTotalPremium()).thenReturn(BigDecimal.ZERO);
            when(projection.getTotalInBase()).thenReturn(BigDecimal.ZERO);

            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByCountry(any(), any(), any(), any(), any()))
                    .thenReturn(List.of(projection));

            List<PolicyReportByCountryDTO> result = policyReportService.getReportByCountry(VALID_FILTER);

            assertThat(result.get(0).policyCount()).isZero();
        }
    }

    @Nested
    @DisplayName("getReportByCounty")
    class GetReportByCounty {

        @Test
        @DisplayName("delegates to repository and maps to DTOs")
        void delegatesAndMaps() throws ValidationException {
            PolicyReportByCountyProjection projection = mock(PolicyReportByCountyProjection.class);
            when(projection.getCountryName()).thenReturn("Romania");
            when(projection.getCountyName()).thenReturn("Bucuresti");
            when(projection.getCurrencyCode()).thenReturn("RON");
            when(projection.getPolicyCount()).thenReturn(2L);
            when(projection.getTotalPremium()).thenReturn(new BigDecimal("200.00"));
            when(projection.getTotalInBase()).thenReturn(new BigDecimal("200.00"));

            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByCounty(any(), any(), any(), any(), any()))
                    .thenReturn(List.of(projection));

            List<PolicyReportByCountyDTO> result = policyReportService.getReportByCounty(VALID_FILTER);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).countyName()).isEqualTo("Bucuresti");
            assertThat(result.get(0).policyCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("getReportByCity")
    class GetReportByCity {

        @Test
        @DisplayName("delegates to repository and maps to DTOs")
        void delegatesAndMaps() throws ValidationException {
            PolicyReportByCityProjection projection = mock(PolicyReportByCityProjection.class);
            when(projection.getCountryName()).thenReturn("Romania");
            when(projection.getCountyName()).thenReturn("Bucuresti");
            when(projection.getCityName()).thenReturn("Sector 1");
            when(projection.getCurrencyCode()).thenReturn("EUR");
            when(projection.getPolicyCount()).thenReturn(1L);
            when(projection.getTotalPremium()).thenReturn(new BigDecimal("120.00"));
            when(projection.getTotalInBase()).thenReturn(new BigDecimal("24.12"));

            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByCity(any(), any(), any(), any(), any()))
                    .thenReturn(List.of(projection));

            List<PolicyReportByCityDTO> result = policyReportService.getReportByCity(VALID_FILTER);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).cityName()).isEqualTo("Sector 1");
            assertThat(result.get(0).policyCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getReportByBroker")
    class GetReportByBroker {

        @Test
        @DisplayName("delegates to repository and maps to DTOs")
        void delegatesAndMaps() throws ValidationException {
            PolicyReportByBrokerProjection projection = mock(PolicyReportByBrokerProjection.class);
            when(projection.getBrokerName()).thenReturn("Broker One");
            when(projection.getCurrencyCode()).thenReturn("RON");
            when(projection.getPolicyCount()).thenReturn(3L);
            when(projection.getTotalPremium()).thenReturn(new BigDecimal("300.00"));
            when(projection.getTotalInBase()).thenReturn(new BigDecimal("300.00"));

            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByBroker(any(), any(), any(), any(), any()))
                    .thenReturn(List.of(projection));

            List<PolicyReportByBrokerDTO> result = policyReportService.getReportByBroker(VALID_FILTER);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).brokerName()).isEqualTo("Broker One");
            assertThat(result.get(0).policyCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("empty result – returns empty list")
        void emptyResult_returnsEmptyList() throws ValidationException {
            doNothing().when(reportValidator).validate(any());
            when(policyRepository.findReportByBroker(any(), any(), any(), any(), any()))
                    .thenReturn(List.of());

            List<PolicyReportByBrokerDTO> result = policyReportService.getReportByBroker(VALID_FILTER);

            assertThat(result).isEmpty();
        }
    }
}
