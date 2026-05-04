package com.endava.insurance.insurance_service.application.service.premium;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.FeeConfigurationRepository;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyPremiumCalculator")
class PolicyPremiumCalculatorTest {

    @Mock
    private FeeConfigurationRepository feeConfigurationRepository;

    @Mock
    private RiskFactorConfigurationRepository riskFactorConfigurationRepository;

    @InjectMocks
    private PolicyPremiumCalculator calculator;

    @Test
    @DisplayName("calculateFinalPremium with no fees and no risk factors returns base premium")
    void noFeesNoRiskFactors_returnsBasePremium() {
        Building building = buildingWithNoGeographyNoRiskFactors();
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of());

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("calculateFinalPremium with one fee adds percentage to base")
    void withOneFee_addsPercentage() {
        Building building = buildingWithNoGeographyNoRiskFactors();
        FeeConfiguration fee = mock(FeeConfiguration.class);
        when(fee.getPercentage()).thenReturn(new BigDecimal("5"));
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of(fee));

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("105.00");
    }

    @Test
    @DisplayName("calculateFinalPremium with building without city returns base plus fees only")
    void buildingWithoutCity_usesOnlyFees() {
        Building building = mock(Building.class);
        when(building.getCity()).thenReturn(null);
        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.getRiskFactors()).thenReturn(Set.of());
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of());

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("100.00");
    }

    private static Building buildingWithNoGeographyNoRiskFactors() {
        Building building = mock(Building.class);
        when(building.getCity()).thenReturn(null);
        when(building.getType()).thenReturn(null);
        when(building.getRiskFactors()).thenReturn(Set.of());
        return building;
    }

    @Test
    @DisplayName("calculateFinalPremium with geographic risk factors adds adjustment")
    void withGeographicRiskFactors_addsAdjustment() {
        Country country = mock(Country.class);
        when(country.getId()).thenReturn(1L);
        County county = mock(County.class);
        when(county.getId()).thenReturn(2L);
        when(county.getCountry()).thenReturn(country);
        City city = mock(City.class);
        when(city.getId()).thenReturn(3L);
        when(city.getCounty()).thenReturn(county);
        Building building = mock(Building.class);
        when(building.getCity()).thenReturn(city);
        when(building.getType()).thenReturn(null);
        when(building.getRiskFactors()).thenReturn(Set.of());

        RiskFactorConfiguration countryConfig = mock(RiskFactorConfiguration.class);
        when(countryConfig.getAdjustmentPercentage()).thenReturn(new BigDecimal("10"));
        when(riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(
                RiskFactorConfigLevel.COUNTRY, "1")).thenReturn(List.of(countryConfig));
        when(riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(
                RiskFactorConfigLevel.COUNTY, "2")).thenReturn(List.of());
        when(riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(
                RiskFactorConfigLevel.CITY, "3")).thenReturn(List.of());
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of());

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("110.00");
    }

    @Test
    @DisplayName("calculateFinalPremium with building type risk factor adds adjustment")
    void withBuildingTypeRiskFactor_addsAdjustment() {
        Building building = mock(Building.class);
        when(building.getCity()).thenReturn(null);
        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.getRiskFactors()).thenReturn(Set.of());

        RiskFactorConfiguration typeConfig = mock(RiskFactorConfiguration.class);
        when(typeConfig.getAdjustmentPercentage()).thenReturn(new BigDecimal("5"));
        when(riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(
                RiskFactorConfigLevel.BUILDING_TYPE, "RESIDENTIAL")).thenReturn(List.of(typeConfig));
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of());

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("105.00");
    }

    @Test
    @DisplayName("calculateFinalPremium with building risk factors adds adjustment")
    void withBuildingRiskFactors_addsAdjustment() {
        RiskFactor riskFactor = mock(RiskFactor.class);
        when(riskFactor.getType()).thenReturn(RiskFactorType.FLOOD_ZONE);
        Building building = mock(Building.class);
        when(building.getCity()).thenReturn(null);
        when(building.getType()).thenReturn(null);
        when(building.getRiskFactors()).thenReturn(Set.of(riskFactor));

        RiskFactorConfiguration rfConfig = mock(RiskFactorConfiguration.class);
        when(rfConfig.getAdjustmentPercentage()).thenReturn(new BigDecimal("3"));
        when(riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(
                RiskFactorConfigLevel.RISK_FACTOR_TYPE, "FLOOD_ZONE")).thenReturn(List.of(rfConfig));
        when(feeConfigurationRepository.findActiveForDate(LocalDate.now(ZoneOffset.UTC))).thenReturn(List.of());

        BigDecimal result = calculator.calculateFinalPremium(new BigDecimal("100"), building, LocalDate.now(ZoneOffset.UTC));

        assertThat(result).isEqualByComparingTo("103.00");
    }
}
