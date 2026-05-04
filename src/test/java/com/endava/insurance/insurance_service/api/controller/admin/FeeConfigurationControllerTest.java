package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.FeeConfigurationService;
import com.endava.insurance.insurance_service.domain.enums.FeeType;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeConfigurationController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "ADMIN")
@DisplayName("FeeConfigurationController")
class FeeConfigurationControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeeConfigurationService feeConfigurationService;

    private static final FeeConfigurationResponseDTO SAMPLE_RESPONSE = new FeeConfigurationResponseDTO(
            1L, "Admin Fee", FeeType.ADMIN_FEE, new BigDecimal("5.00"),
            LocalDate.of(2025, 1, 1), null, true
    );

    @Test
    @DisplayName("POST /api/v2/admin/fees – 201")
    void create_returns201AndBody() throws Exception {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Admin Fee", FeeType.ADMIN_FEE, new BigDecimal("5.00"),
                LocalDate.of(2025, 1, 1), null, true
        );
        when(feeConfigurationService.create(any(FeeConfigurationRequestDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/admin/fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Admin Fee"))
                .andExpect(jsonPath("$.type").value("ADMIN_FEE"))
                .andExpect(jsonPath("$.percentage").value(5.0))
                .andExpect(jsonPath("$.active").value(true));

        verify(feeConfigurationService).create(any(FeeConfigurationRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/admin/fees/{id} – 200")
    void update_returns200AndBody() throws Exception {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Updated Fee", FeeType.BROKER_COMMISSION, new BigDecimal("8.00"), null, null, true
        );
        FeeConfigurationResponseDTO updated = new FeeConfigurationResponseDTO(
                1L, "Updated Fee", FeeType.BROKER_COMMISSION, new BigDecimal("8.00"), null, null, true
        );
        when(feeConfigurationService.update(eq(1L), any(FeeConfigurationRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v2/admin/fees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Fee"))
                .andExpect(jsonPath("$.type").value("BROKER_COMMISSION"))
                .andExpect(jsonPath("$.percentage").value(8.0));

        verify(feeConfigurationService).update(eq(1L), any(FeeConfigurationRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/v2/admin/fees/{id} – 200")
    void getById_returns200AndBody() throws Exception {
        when(feeConfigurationService.getById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/admin/fees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Admin Fee"));

        verify(feeConfigurationService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/fees/{id} – 404")
    void getById_notFound_returns404() throws Exception {
        when(feeConfigurationService.getById(999L)).thenThrow(new ResourceNotFoundException("Fee not found"));

        mockMvc.perform(get("/api/v2/admin/fees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(feeConfigurationService).getById(999L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/fees – 200, pagină")
    void getAll_returns200AndPage() throws Exception {
        when(feeConfigurationService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/admin/fees?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Admin Fee"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(feeConfigurationService).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/v2/admin/fees – 400 la ValidationException")
    void create_validationException_returns400() throws Exception {
        FeeConfigurationRequestDTO request = new FeeConfigurationRequestDTO(
                "Fee", FeeType.ADMIN_FEE, new BigDecimal("5.00"), null, null, true
        );
        when(feeConfigurationService.create(any(FeeConfigurationRequestDTO.class)))
                .thenThrow(new ValidationException("Invalid fee configuration"));

        mockMvc.perform(post("/api/v2/admin/fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
}
