package com.endava.insurance.insurance_service.application.mapper.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.domain.enums.FeeType;
import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FeeConfigurationMapper")
class FeeConfigurationMapperTest {

    private final FeeConfigurationMapper mapper = new FeeConfigurationMapper();

    @Test
    @DisplayName("toEntity maps request to FeeConfiguration")
    void toEntity_mapsRequest() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Admin Fee", FeeType.ADMIN_FEE, new BigDecimal("2.50"), from, to, true);
        FeeConfiguration entity = mapper.toEntity(request);
        assertThat(entity.getName()).isEqualTo("Admin Fee");
        assertThat(entity.getType()).isEqualTo(FeeType.ADMIN_FEE);
        assertThat(entity.getPercentage()).isEqualByComparingTo("2.50");
        assertThat(entity.getEffectiveFrom()).isEqualTo(from);
        assertThat(entity.getEffectiveTo()).isEqualTo(to);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    @DisplayName("toResponse maps FeeConfiguration to DTO")
    void toResponse_mapsToDto() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);
        FeeConfiguration config = new FeeConfiguration(
                "Broker Fee", FeeType.BROKER_COMMISSION, new BigDecimal("3.00"), from, to, true);
        FeeConfigurationResponseDTO dto = mapper.toResponse(config);
        assertThat(dto.name()).isEqualTo("Broker Fee");
        assertThat(dto.type()).isEqualTo(FeeType.BROKER_COMMISSION);
        assertThat(dto.percentage()).isEqualByComparingTo("3.00");
        assertThat(dto.effectiveFrom()).isEqualTo(from);
        assertThat(dto.effectiveTo()).isEqualTo(to);
        assertThat(dto.active()).isTrue();
    }

    @Test
    @DisplayName("updateEntityFromRequest updates entity")
    void updateEntityFromRequest_updatesEntity() {
        FeeConfiguration config = new FeeConfiguration(
                "Old", FeeType.ADMIN_FEE, new BigDecimal("1"), null, null, true);
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Updated", FeeType.BROKER_COMMISSION, new BigDecimal("5"), LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC).plusMonths(6), false);
        mapper.updateEntityFromRequest(request, config);
        assertThat(config.getName()).isEqualTo("Updated");
        assertThat(config.getType()).isEqualTo(FeeType.BROKER_COMMISSION);
        assertThat(config.getPercentage()).isEqualByComparingTo("5");
        assertThat(config.isActive()).isFalse();
    }
}
