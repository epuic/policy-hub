package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCancelDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.PolicyService;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "BROKER")
@DisplayName("PolicyController")
class PolicyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PolicyService policyService;

    private static final PolicyResponseDTO SAMPLE_RESPONSE = new PolicyResponseDTO(
            1L, "POL-001", 10L, "Client Name", 20L, "Str. X nr. 1",
            "Bucuresti", "Bucuresti", "Romania", 1L, "Broker One",
            PolicyStatus.DRAFT, LocalDate.of(2025, 6, 1), LocalDate.of(2026, 5, 31),
            new BigDecimal("100.00"), "EUR", new BigDecimal("120.00"),
            LocalDateTime.of(2025, 2, 1, 0, 0), LocalDateTime.of(2025, 2, 1, 0, 0),
            null, null
    );

    @Test
    @DisplayName("GET /api/v2/brokers/policies – 200, pagină filtrată")
    void getPolicies_returns200AndPage() throws Exception {
        when(policyService.getFiltered(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/brokers/policies?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].policyNumber").value("POL-001"))
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(policyService).getFiltered(eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v2/brokers/policies/{id} – 200")
    void getPolicyById_returns200AndBody() throws Exception {
        when(policyService.getById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/brokers/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.policyNumber").value("POL-001"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(policyService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/v2/brokers/policies/{id} – 404")
    void getPolicyById_notFound_returns404() throws Exception {
        when(policyService.getById(999L)).thenThrow(new ResourceNotFoundException("Policy not found"));

        mockMvc.perform(get("/api/v2/brokers/policies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(policyService).getById(999L);
    }

    @Test
    @DisplayName("POST /api/v2/brokers/policies – 201, create draft")
    void createDraft_returns201AndBody() throws Exception {
        PolicyCreateDTO request = new PolicyCreateDTO(
                10L, 20L, 1L,
                LocalDate.of(2025, 6, 1), LocalDate.of(2026, 5, 31),
                new BigDecimal("100.00"), 1L
        );
        when(policyService.createDraft(any(PolicyCreateDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.policyNumber").value("POL-001"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(policyService).createDraft(any(PolicyCreateDTO.class));
    }

    @Test
    @DisplayName("POST /api/v2/brokers/policies/{id}/activate – 200")
    void activate_returns200() throws Exception {
        PolicyResponseDTO active = new PolicyResponseDTO(
                1L, "POL-001", 10L, "Client", 20L, "Addr",
                "City", "County", "Country", 1L, "Broker",
                PolicyStatus.ACTIVE, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC).plusYears(1),
                new BigDecimal("100"), "EUR", new BigDecimal("120"),
                LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), null, null
        );
        when(policyService.activate(1L)).thenReturn(active);

        mockMvc.perform(post("/api/v2/brokers/policies/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(policyService).activate(1L);
    }

    @Test
    @DisplayName("POST /api/v2/brokers/policies/{id}/cancel – 200")
    void cancel_returns200() throws Exception {
        PolicyCancelDTO request = new PolicyCancelDTO("Client requested cancellation");
        PolicyResponseDTO cancelled = new PolicyResponseDTO(
                1L, "POL-001", 10L, "Client", 20L, "Addr",
                "City", "County", "Country", 1L, "Broker",
                PolicyStatus.CANCELLED, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC),
                new BigDecimal("100"), "EUR", new BigDecimal("120"),
                LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC), "Client requested cancellation"
        );
        when(policyService.cancel(eq(1L), any(PolicyCancelDTO.class))).thenReturn(cancelled);

        mockMvc.perform(post("/api/v2/brokers/policies/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancellationReason").value("Client requested cancellation"));

        verify(policyService).cancel(eq(1L), any(PolicyCancelDTO.class));
    }

    @Test
    @DisplayName("POST /api/v2/brokers/policies – 400 la ValidationException")
    void createDraft_validationException_returns400() throws Exception {
        PolicyCreateDTO request = new PolicyCreateDTO(
                10L, 20L, 1L, LocalDate.of(2025, 6, 1), LocalDate.of(2026, 5, 31),
                new BigDecimal("100.00"), 1L
        );
        when(policyService.createDraft(any(PolicyCreateDTO.class)))
                .thenThrow(new ValidationException("End date must be after start date"));

        mockMvc.perform(post("/api/v2/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
}
