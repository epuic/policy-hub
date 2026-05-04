package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.application.service.contract.BuildingService;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuildingControllerV2.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "BROKER")
@DisplayName("BuildingControllerV2")
class BuildingControllerV2Test extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    private static final List<PolicySummaryDTO> SAMPLE_POLICIES = List.of(
            new PolicySummaryDTO(1L, "POL-001", PolicyStatus.ACTIVE,
                    LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1),
                    new BigDecimal("120.00"), "EUR", LocalDateTime.of(2025, 1, 1, 0, 0))
    );

    private static final BuildingResponseDTOV2 SAMPLE_RESPONSE = new BuildingResponseDTOV2(
            1L, 10L, "Client Name", "Str. X nr. 1, Bucuresti",
            "Bucuresti", "Bucuresti", "Romania", 2000,
            BuildingType.RESIDENTIAL, 3, 85.5, 150000.0,
            List.of(RiskFactorType.EARTHQUAKE_RISK_ZONE), SAMPLE_POLICIES
    );

    @Test
    @DisplayName("GET /api/v2/brokers/clients/{clientId}/buildings – 200, pagină V2")
    void getClientsBuildings_returns200AndPage() throws Exception {
        when(buildingService.getBuildingsByClientIdV2(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/brokers/clients/10/buildings?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("Client Name"))
                .andExpect(jsonPath("$.content[0].policies").isArray())
                .andExpect(jsonPath("$.content[0].policies[0].policyNumber").value("POL-001"))
                .andExpect(jsonPath("$.content[0].policies[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(buildingService).getBuildingsByClientIdV2(eq(10L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v2/brokers/buildings/{buildingId} – 200, building V2 cu policies")
    void getBuildingById_returns200AndBody() throws Exception {
        when(buildingService.getBuildingByIdV2(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/brokers/buildings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullAddress").value("Str. X nr. 1, Bucuresti"))
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0].policyNumber").value("POL-001"));

        verify(buildingService).getBuildingByIdV2(1L);
    }

    @Test
    @DisplayName("GET /api/v2/brokers/buildings/{buildingId} – 404")
    void getBuildingById_notFound_returns404() throws Exception {
        when(buildingService.getBuildingByIdV2(999L)).thenThrow(new ResourceNotFoundException("Building not found"));

        mockMvc.perform(get("/api/v2/brokers/buildings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(buildingService).getBuildingByIdV2(999L);
    }
}
