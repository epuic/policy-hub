package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.RiskFactorConfigurationService;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskFactorConfigurationController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "ADMIN")
@DisplayName("RiskFactorConfigurationController")
class RiskFactorConfigurationControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RiskFactorConfigurationService riskFactorConfigurationService;

    private static final RiskFactorConfigurationResponseDTO SAMPLE_RESPONSE = new RiskFactorConfigurationResponseDTO(
            1L, RiskFactorConfigLevel.COUNTRY, "RO", "Romania",
            new BigDecimal("10.00"), true
    );

    @Test
    @DisplayName("POST /api/v2/admin/risk-factors – 201")
    void create_returns201AndBody() throws Exception {
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.COUNTRY, "RO", new BigDecimal("10.00"), true
        );
        when(riskFactorConfigurationService.create(any(RiskFactorConfigurationRequestDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/admin/risk-factors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("COUNTRY"))
                .andExpect(jsonPath("$.referenceId").value("RO"))
                .andExpect(jsonPath("$.referenceName").value("Romania"))
                .andExpect(jsonPath("$.adjustmentPercentage").value(10.0))
                .andExpect(jsonPath("$.active").value(true));

        verify(riskFactorConfigurationService).create(any(RiskFactorConfigurationRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/admin/risk-factors/{id} – 200")
    void update_returns200AndBody() throws Exception {
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.CITY, "1", new BigDecimal("5.00"), true
        );
        RiskFactorConfigurationResponseDTO updated = new RiskFactorConfigurationResponseDTO(
                1L, RiskFactorConfigLevel.CITY, "1", "Bucuresti", new BigDecimal("5.00"), true
        );
        when(riskFactorConfigurationService.update(eq(1L), any(RiskFactorConfigurationRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v2/admin/risk-factors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value("CITY"))
                .andExpect(jsonPath("$.adjustmentPercentage").value(5.0));

        verify(riskFactorConfigurationService).update(eq(1L), any(RiskFactorConfigurationRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/v2/admin/risk-factors/{id} – 200")
    void getById_returns200AndBody() throws Exception {
        when(riskFactorConfigurationService.getById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/admin/risk-factors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("COUNTRY"));

        verify(riskFactorConfigurationService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/risk-factors/{id} – 404")
    void getById_notFound_returns404() throws Exception {
        when(riskFactorConfigurationService.getById(999L)).thenThrow(new ResourceNotFoundException("Risk factor not found"));

        mockMvc.perform(get("/api/v2/admin/risk-factors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(riskFactorConfigurationService).getById(999L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/risk-factors – 200, pagină")
    void getAll_returns200AndPage() throws Exception {
        when(riskFactorConfigurationService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/admin/risk-factors?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].level").value("COUNTRY"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(riskFactorConfigurationService).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/v2/admin/risk-factors – 400 la ValidationException")
    void create_validationException_returns400() throws Exception {
        RiskFactorConfigurationRequestDTO request = new RiskFactorConfigurationRequestDTO(
                RiskFactorConfigLevel.COUNTRY, "RO", new BigDecimal("10.00"), true
        );
        when(riskFactorConfigurationService.create(any(RiskFactorConfigurationRequestDTO.class)))
                .thenThrow(new ValidationException("Duplicate configuration"));

        mockMvc.perform(post("/api/v2/admin/risk-factors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Duplicate configuration"));
    }
}
