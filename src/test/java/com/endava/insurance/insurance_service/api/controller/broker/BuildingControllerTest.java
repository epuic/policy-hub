package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.BuildingService;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuildingController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "BROKER")
@DisplayName("BuildingController")
class BuildingControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuildingService buildingService;

    private static final BuildingResponseDTO SAMPLE_RESPONSE = new BuildingResponseDTO(
            1L, 10L, "Client Name", "Str. X nr. 1, Bucuresti",
            "Bucuresti", "Bucuresti", "Romania", 2000,
            BuildingType.RESIDENTIAL, 3, 85.5, 150000.0,
            List.of(RiskFactorType.EARTHQUAKE_RISK_ZONE)
    );

    @Test
    @DisplayName("POST /api/brokers/clients/{clientId}/buildings – 201")
    void createBuilding_returns201AndBody() throws Exception {
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Str. X", "1", 1L, 2000, BuildingType.RESIDENTIAL,
                3, 85.5, 150000.0, List.of(RiskFactorType.EARTHQUAKE_RISK_ZONE)
        );
        when(buildingService.createBuilding(eq(10L), any(BuildingRequestDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/brokers/clients/10/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(10))
                .andExpect(jsonPath("$.fullAddress").value("Str. X nr. 1, Bucuresti"))
                .andExpect(jsonPath("$.type").value("RESIDENTIAL"))
                .andExpect(jsonPath("$.surfaceArea").value(85.5))
                .andExpect(jsonPath("$.insuredValue").value(150000.0));

        verify(buildingService).createBuilding(eq(10L), any(BuildingRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{clientId}/buildings – 200, pagină")
    void getClientsBuildings_returns200AndPage() throws Exception {
        when(buildingService.getBuildingsByClientId(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/brokers/clients/10/buildings?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("Client Name"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(buildingService).getBuildingsByClientId(eq(10L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/buildings/{buildingId} – 200")
    void getBuildingById_returns200AndBody() throws Exception {
        when(buildingService.getBuildingById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/brokers/buildings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullAddress").value("Str. X nr. 1, Bucuresti"));

        verify(buildingService).getBuildingById(1L);
    }

    @Test
    @DisplayName("GET /api/brokers/buildings/{buildingId} – 404")
    void getBuildingById_notFound_returns404() throws Exception {
        when(buildingService.getBuildingById(999L)).thenThrow(new ResourceNotFoundException("Building not found"));

        mockMvc.perform(get("/api/brokers/buildings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(buildingService).getBuildingById(999L);
    }

    @Test
    @DisplayName("PUT /api/brokers/buildings/{buildingId} – 200")
    void updateBuilding_returns200AndBody() throws Exception {
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Str. Y", "2", 1L, 2001, BuildingType.INDUSTRIAL,
                2, 120.0, 200000.0, List.of()
        );
        BuildingResponseDTO updated = new BuildingResponseDTO(
                1L, 10L, "Client Name", "Str. Y nr. 2, Bucuresti",
                "Bucuresti", "Bucuresti", "Romania", 2001,
                BuildingType.INDUSTRIAL, 2, 120.0, 200000.0, List.of()
        );
        when(buildingService.updateBuilding(eq(1L), any(BuildingRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/brokers/buildings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullAddress").value("Str. Y nr. 2, Bucuresti"))
                .andExpect(jsonPath("$.type").value("INDUSTRIAL"))
                .andExpect(jsonPath("$.surfaceArea").value(120.0));

        verify(buildingService).updateBuilding(eq(1L), any(BuildingRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/brokers/clients/{clientId}/buildings – 400 la ValidationException")
    void createBuilding_validationException_returns400() throws Exception {
        BuildingRequestDTO request = new BuildingRequestDTO(
                "Str. X", "1", 1L, 2000, BuildingType.RESIDENTIAL, 3, 85.5, 150000.0, List.of()
        );
        when(buildingService.createBuilding(eq(10L), any(BuildingRequestDTO.class)))
                .thenThrow(new ValidationException("City not found"));

        mockMvc.perform(post("/api/brokers/clients/10/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("City not found"));
    }
}
