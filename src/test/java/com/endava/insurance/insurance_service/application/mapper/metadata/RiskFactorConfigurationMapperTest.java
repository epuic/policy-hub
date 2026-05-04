package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskFactorConfigurationMapper")
class RiskFactorConfigurationMapperTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountyRepository countyRepository;
    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private RiskFactorConfigurationMapper mapper;

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("maps request to entity")
        void mapsRequestToEntity() {
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.COUNTRY, "1", new BigDecimal("5.00"), true);
            RiskFactorConfiguration entity = mapper.toEntity(request);
            assertThat(entity.getLevel()).isEqualTo(RiskFactorConfigLevel.COUNTRY);
            assertThat(entity.getReferenceId()).isEqualTo("1");
            assertThat(entity.getAdjustmentPercentage()).isEqualByComparingTo("5.00");
            assertThat(entity.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps entity to response with reference name from country repo")
        void mapsWithCountryReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.COUNTRY, "1", new BigDecimal("10"), true);
            var country = mock(com.endava.insurance.insurance_service.domain.model.geography.Country.class);
            when(country.getName()).thenReturn("Romania");
            when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.id()).isNull();
            assertThat(dto.level()).isEqualTo(RiskFactorConfigLevel.COUNTRY);
            assertThat(dto.referenceId()).isEqualTo("1");
            assertThat(dto.referenceName()).isEqualTo("Romania");
            assertThat(dto.adjustmentPercentage()).isEqualByComparingTo("10");
            assertThat(dto.active()).isTrue();
        }

        @Test
        @DisplayName("maps entity when referenceId null returns null referenceName")
        void nullReferenceId_returnsNullReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.BUILDING_TYPE, null, new BigDecimal("0"), true);

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isNull();
        }

        @Test
        @DisplayName("maps entity when referenceId blank returns null referenceName")
        void blankReferenceId_returnsNullReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.COUNTRY, "  ", new BigDecimal("0"), true);

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isNull();
        }

        @Test
        @DisplayName("maps entity with COUNTY level resolves county name")
        void mapsWithCountyReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.COUNTY, "2", new BigDecimal("5"), true);
            var county = mock(com.endava.insurance.insurance_service.domain.model.geography.County.class);
            when(county.getName()).thenReturn("Bucuresti");
            when(countyRepository.findById(2L)).thenReturn(Optional.of(county));

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isEqualTo("Bucuresti");
        }

        @Test
        @DisplayName("maps entity with CITY level resolves city name")
        void mapsWithCityReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.CITY, "3", new BigDecimal("5"), true);
            var city = mock(com.endava.insurance.insurance_service.domain.model.geography.City.class);
            when(city.getName()).thenReturn("Sector 1");
            when(cityRepository.findById(3L)).thenReturn(Optional.of(city));

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isEqualTo("Sector 1");
        }

        @Test
        @DisplayName("maps entity with BUILDING_TYPE level resolves enum name")
        void mapsWithBuildingTypeReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.BUILDING_TYPE, "RESIDENTIAL", new BigDecimal("5"), true);

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isEqualTo("RESIDENTIAL");
        }

        @Test
        @DisplayName("maps entity with RISK_FACTOR_TYPE level resolves enum name")
        void mapsWithRiskFactorTypeReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.RISK_FACTOR_TYPE, "FLOOD_ZONE", new BigDecimal("5"), true);

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isEqualTo("FLOOD_ZONE");
        }

        @Test
        @DisplayName("maps entity with BUILDING_TYPE invalid referenceId returns null")
        void mapsWithBuildingTypeInvalidReferenceId_returnsNull() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.BUILDING_TYPE, "INVALID_TYPE", new BigDecimal("5"), true);

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isNull();
        }

        @Test
        @DisplayName("maps entity when country not found returns null referenceName")
        void countryNotFound_returnsNullReferenceName() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.COUNTRY, "99", new BigDecimal("0"), true);
            when(countryRepository.findById(99L)).thenReturn(Optional.empty());

            RiskFactorConfigurationResponseDTO dto = mapper.toResponse(config);

            assertThat(dto.referenceName()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntityFromRequest")
    class UpdateEntityFromRequest {

        @Test
        @DisplayName("updates entity from request")
        void updatesEntity() {
            RiskFactorConfiguration config = new RiskFactorConfiguration(
                    RiskFactorConfigLevel.CITY, "10", new BigDecimal("5"), true);
            RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                    RiskFactorConfigLevel.CITY, "20", new BigDecimal("15"), false);

            mapper.updateEntityFromRequest(request, config);

            assertThat(config.getReferenceId()).isEqualTo("20");
            assertThat(config.getAdjustmentPercentage()).isEqualByComparingTo("15");
            assertThat(config.isActive()).isFalse();
        }
    }
}
