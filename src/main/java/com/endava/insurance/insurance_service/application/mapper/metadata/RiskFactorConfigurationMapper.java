package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiskFactorConfigurationMapper {

    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;

    public RiskFactorConfiguration toEntity(RiskFactorConfigurationRequestDTO request) {
        return new RiskFactorConfiguration(
                request.level(),
                request.referenceId(),
                request.adjustmentPercentage(),
                request.active()
        );
    }

    public RiskFactorConfigurationResponseDTO toResponse(RiskFactorConfiguration riskFactorConfiguration) {
        String referenceName = resolveReferenceName(riskFactorConfiguration.getLevel(), riskFactorConfiguration.getReferenceId());
        return new RiskFactorConfigurationResponseDTO(
                riskFactorConfiguration.getId(),
                riskFactorConfiguration.getLevel(),
                riskFactorConfiguration.getReferenceId(),
                referenceName,
                riskFactorConfiguration.getAdjustmentPercentage(),
                riskFactorConfiguration.isActive()
        );
    }

    private String resolveReferenceName(RiskFactorConfigLevel level, String referenceId) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            return null;
        }

        try {
            Long id = Long.parseLong(referenceId.trim());
            return switch (level) {
                case COUNTRY -> countryRepository.findById(id)
                        .map(Country::getName)
                        .orElse(null);
                case COUNTY -> countyRepository.findById(id)
                        .map(County::getName)
                        .orElse(null);
                case CITY -> cityRepository.findById(id)
                        .map(City::getName)
                        .orElse(null);
                case BUILDING_TYPE -> resolveBuildingTypeName(referenceId.trim());
                case RISK_FACTOR_TYPE -> resolveRiskFactorTypeName(referenceId.trim());
            };
        } catch (NumberFormatException e) {
            if (level == RiskFactorConfigLevel.BUILDING_TYPE) {
                return resolveBuildingTypeName(referenceId.trim());
            }
            if (level == RiskFactorConfigLevel.RISK_FACTOR_TYPE) {
                return resolveRiskFactorTypeName(referenceId.trim());
            }
            return null;
        }
    }

    private static String resolveBuildingTypeName(String referenceId) {
        try {
            return BuildingType.valueOf(referenceId).name();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String resolveRiskFactorTypeName(String referenceId) {
        try {
            return RiskFactorType.valueOf(referenceId).name();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void updateEntityFromRequest(RiskFactorConfigurationRequestDTO request, RiskFactorConfiguration riskFactorConfiguration) {
        riskFactorConfiguration.update(
                request.referenceId(),
                request.adjustmentPercentage(),
                request.active()
        );
    }
}
