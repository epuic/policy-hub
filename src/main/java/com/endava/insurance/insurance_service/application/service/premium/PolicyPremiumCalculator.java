package com.endava.insurance.insurance_service.application.service.premium;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.FeeConfigurationRepository;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PolicyPremiumCalculator {

    private final FeeConfigurationRepository feeConfigurationRepository;
    private final RiskFactorConfigurationRepository riskFactorConfigurationRepository;

    public BigDecimal calculateFinalPremium(BigDecimal basePremium, Building building, LocalDate policyStartDate) {
        BigDecimal totalPercentageAdjustment = BigDecimal.ZERO;

        totalPercentageAdjustment = totalPercentageAdjustment.add(addFeeConfigurations(policyStartDate));
        totalPercentageAdjustment = totalPercentageAdjustment.add(addGeographicRiskFactors(building));
        totalPercentageAdjustment = totalPercentageAdjustment.add(addBuildingTypeRiskFactors(building));
        totalPercentageAdjustment = totalPercentageAdjustment.add(addBuildingRiskFactors(building));

        BigDecimal multiplier = calculateMultiplier(totalPercentageAdjustment);
        return basePremium.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal addFeeConfigurations(LocalDate policyStartDate) {
        BigDecimal adjustment = BigDecimal.ZERO;
        List<FeeConfiguration> activeFees = feeConfigurationRepository.findActiveForDate(policyStartDate);
        for (FeeConfiguration fee : activeFees) {
            adjustment = adjustment.add(fee.getPercentage());
        }
        return adjustment;
    }

    private BigDecimal addGeographicRiskFactors(Building building) {
        BigDecimal adjustment = BigDecimal.ZERO;

        if (building.getCity() == null || building.getCity().getCounty() == null ||
            building.getCity().getCounty().getCountry() == null) {
            return adjustment;
        }

        Long countryId = building.getCity().getCounty().getCountry().getId();
        Long countyId = building.getCity().getCounty().getId();
        Long cityId = building.getCity().getId();

        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTRY, countryId.toString()));
        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTY, countyId.toString()));
        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.CITY, cityId.toString()));

        return adjustment;
    }

    private BigDecimal addBuildingTypeRiskFactors(Building building) {
        if (building.getType() == null) {
            return BigDecimal.ZERO;
        }

        return sumRiskFactorConfigs(
                RiskFactorConfigLevel.BUILDING_TYPE,
                building.getType().name()
        );
    }

    private BigDecimal addBuildingRiskFactors(Building building) {
        BigDecimal adjustment = BigDecimal.ZERO;

        if (building.getRiskFactors() == null || building.getRiskFactors().isEmpty()) {
            return adjustment;
        }

        for (RiskFactor riskFactor : building.getRiskFactors()) {
            RiskFactorType riskFactorType = riskFactor.getType();
            adjustment = adjustment.add(sumRiskFactorConfigs(
                    RiskFactorConfigLevel.RISK_FACTOR_TYPE,
                    riskFactorType.name()
            ));
        }

        return adjustment;
    }

    private BigDecimal sumRiskFactorConfigs(RiskFactorConfigLevel level, String referenceId) {
        BigDecimal adjustment = BigDecimal.ZERO;
        List<RiskFactorConfiguration> configs = riskFactorConfigurationRepository
                .findByActiveTrueAndLevelAndReferenceId(level, referenceId);
        for (RiskFactorConfiguration config : configs) {
            adjustment = adjustment.add(config.getAdjustmentPercentage());
        }
        return adjustment;
    }

    private BigDecimal calculateMultiplier(BigDecimal totalPercentageAdjustment) {
        return BigDecimal.ONE.add(
                totalPercentageAdjustment.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );
    }
}
