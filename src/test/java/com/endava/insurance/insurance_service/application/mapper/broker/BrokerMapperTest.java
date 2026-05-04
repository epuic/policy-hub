package com.endava.insurance.insurance_service.application.mapper.broker;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.model.Broker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BrokerMapper")
class BrokerMapperTest {

    private final BrokerMapper mapper = new BrokerMapper();

    @Test
    @DisplayName("toEntity maps create DTO to Broker")
    void toEntity_mapsCreateDto() throws Exception {
        BrokerCreateDTO dto = new BrokerCreateDTO("BRK1", "Broker One", "b@b.com", "+40123456789", "password123", BrokerStatus.ACTIVE, new BigDecimal("10"));
        Broker broker = mapper.toEntity(dto);
        assertThat(broker.getBrokerCode()).isEqualTo("BRK1");
        assertThat(broker.getName()).isEqualTo("Broker One");
        assertThat(broker.getEmail()).isEqualTo("b@b.com");
        assertThat(broker.getPhone()).isEqualTo("+40123456789");
        assertThat(broker.getStatus()).isEqualTo(BrokerStatus.ACTIVE);
        assertThat(broker.getCommissionPercentage()).isEqualByComparingTo("10");
    }

    @Test
    @DisplayName("toResponse maps Broker to response DTO")
    void toResponse_mapsToDto() throws Exception {
        Broker broker = new Broker("BRK1", "Broker One", "b@b.com", "+40123456789", BrokerStatus.ACTIVE, new BigDecimal("5"));
        BrokerResponseDTO dto = mapper.toResponse(broker);
        assertThat(dto.brokerCode()).isEqualTo("BRK1");
        assertThat(dto.name()).isEqualTo("Broker One");
        assertThat(dto.email()).isEqualTo("b@b.com");
        assertThat(dto.phone()).isEqualTo("+40123456789");
        assertThat(dto.status()).isEqualTo(BrokerStatus.ACTIVE);
        assertThat(dto.commissionPercentage()).isEqualByComparingTo("5");
    }

    @Test
    @DisplayName("updateEntityFromRequest updates broker from DTO")
    void updateEntityFromRequest_updatesBroker() throws Exception {
        Broker broker = new Broker("BRK1", "Old", "old@b.com", "+40123456789", BrokerStatus.ACTIVE, null);
        BrokerUpdateDTO update = new BrokerUpdateDTO("New Name", "new@b.com", "+40223456789", new BigDecimal("8"));
        mapper.updateEntityFromRequest(update, broker);
        assertThat(broker.getName()).isEqualTo("New Name");
        assertThat(broker.getEmail()).isEqualTo("new@b.com");
        assertThat(broker.getPhone()).isEqualTo("+40223456789");
        assertThat(broker.getCommissionPercentage()).isEqualByComparingTo("8");
    }
}
