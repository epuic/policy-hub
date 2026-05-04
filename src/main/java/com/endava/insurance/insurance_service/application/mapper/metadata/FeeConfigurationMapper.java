package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import org.springframework.stereotype.Component;

@Component
public class FeeConfigurationMapper {

    public FeeConfiguration toEntity(FeeConfigurationRequestDTO request) {
        return new FeeConfiguration(
                request.name(),
                request.type(),
                request.percentage(),
                request.effectiveFrom(),
                request.effectiveTo(),
                request.active()
        );
    }

    public FeeConfigurationResponseDTO toResponse(FeeConfiguration feeConfiguration) {
        return new FeeConfigurationResponseDTO(
                feeConfiguration.getId(),
                feeConfiguration.getName(),
                feeConfiguration.getType(),
                feeConfiguration.getPercentage(),
                feeConfiguration.getEffectiveFrom(),
                feeConfiguration.getEffectiveTo(),
                feeConfiguration.isActive()
        );
    }

    public void updateEntityFromRequest(FeeConfigurationRequestDTO request, FeeConfiguration feeConfiguration) {
        feeConfiguration.update(
                request.name(),
                request.type(),
                request.percentage(),
                request.effectiveFrom(),
                request.effectiveTo(),
                request.active()
        );
    }
}
