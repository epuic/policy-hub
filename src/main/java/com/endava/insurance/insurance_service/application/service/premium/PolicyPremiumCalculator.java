package com.endava.insurance.insurance_service.application.service.premium;

import com.endava.insurance.insurance_service.application.dto.policy.PremiumAdjustmentDTO;
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
import java.util.ArrayList;
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

    public List<PremiumAdjustmentDTO> getPremiumAdjustments(BigDecimal basePremium, Building building, LocalDate policyStartDate) {
        List<PremiumAdjustmentDTO> adjustments = new ArrayList<>();
        addFeeConfigurationDetails(adjustments, basePremium, policyStartDate);
        addGeographicRiskFactorDetails(adjustments, basePremium, building);
        addBuildingTypeRiskFactorDetails(adjustments, basePremium, building);
        addBuildingRiskFactorDetails(adjustments, basePremium, building);
        return adjustments;
    }

    private BigDecimal addFeeConfigurations(LocalDate policyStartDate) {
        BigDecimal adjustment = BigDecimal.ZERO;
        List<FeeConfiguration> activeFees = feeConfigurationRepository.findActiveForDate(policyStartDate);
        for (FeeConfiguration fee : activeFees) {
            adjustment = adjustment.add(fee.getPercentage());
        }
        return adjustment;
    }

    private void addFeeConfigurationDetails(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium, LocalDate policyStartDate) {
        List<FeeConfiguration> activeFees = feeConfigurationRepository.findActiveForDate(policyStartDate);
        for (FeeConfiguration fee : activeFees) {
            addAdjustment(adjustments, basePremium, "Fee", fee.getName(), fee.getPercentage());
        }
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

    private void addGeographicRiskFactorDetails(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium, Building building) {
        if (building.getCity() == null || building.getCity().getCounty() == null ||
                building.getCity().getCounty().getCountry() == null) {
            return;
        }

        var city = building.getCity();
        var county = city.getCounty();
        var country = county.getCountry();

            addRiskFactorConfigDetails(adjustments, basePremium, RiskFactorConfigLevel.COUNTRY, country.getId().toString(), "Country: " + country.getName());
            addRiskFactorConfigDetails(adjustments, basePremium, RiskFactorConfigLevel.COUNTY, county.getId().toString(), "County: " + county.getName());
            addRiskFactorConfigDetails(adjustments, basePremium, RiskFactorConfigLevel.CITY, city.getId().toString(), "City: " + city.getName());
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

    private void addBuildingTypeRiskFactorDetails(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium, Building building) {
        if (building.getType() == null) {
            return;
        }

        addRiskFactorConfigDetails(
                adjustments,
                basePremium,
                RiskFactorConfigLevel.BUILDING_TYPE,
                building.getType().name(),
                    "Building type: " + building.getType().name()
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

    private void addBuildingRiskFactorDetails(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium, Building building) {
        if (building.getRiskFactors() == null || building.getRiskFactors().isEmpty()) {
            return;
        }

        for (RiskFactor riskFactor : building.getRiskFactors()) {
            RiskFactorType riskFactorType = riskFactor.getType();
            addRiskFactorConfigDetails(
                    adjustments,
                    basePremium,
                    RiskFactorConfigLevel.RISK_FACTOR_TYPE,
                    riskFactorType.name(),
                        "Risk factor: " + riskFactorType.name()
            );
        }
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

    private void addRiskFactorConfigDetails(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium,
                                            RiskFactorConfigLevel level, String referenceId, String label) {
        List<RiskFactorConfiguration> configs = riskFactorConfigurationRepository
                .findByActiveTrueAndLevelAndReferenceId(level, referenceId);
        for (RiskFactorConfiguration config : configs) {
            addAdjustment(adjustments, basePremium, "Risk", label, config.getAdjustmentPercentage());
        }
    }

    private void addAdjustment(List<PremiumAdjustmentDTO> adjustments, BigDecimal basePremium,
                               String category, String label, BigDecimal percentage) {
        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal amount = basePremium
                .multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        adjustments.add(new PremiumAdjustmentDTO(category, label, percentage, amount));
    }

    private BigDecimal calculateMultiplier(BigDecimal totalPercentageAdjustment) {
        return BigDecimal.ONE.add(
                totalPercentageAdjustment.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );
    }
}
