package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.CurrencyService;
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

@WebMvcTest(CurrencyController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "ADMIN")
@DisplayName("CurrencyController")
class CurrencyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    private static final CurrencyResponseDTO SAMPLE_RESPONSE = new CurrencyResponseDTO(
            1L, "EUR", "Euro", new BigDecimal("1.0"), true
    );

    @Test
    @DisplayName("POST /api/v2/admin/currencies – 201")
    void create_returns201AndBody() throws Exception {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", new BigDecimal("1.0"), true);
        when(currencyService.create(any(CurrencyRequestDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/admin/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("EUR"))
                .andExpect(jsonPath("$.name").value("Euro"))
                .andExpect(jsonPath("$.exchangeRateToBase").value(1.0))
                .andExpect(jsonPath("$.active").value(true));

        verify(currencyService).create(any(CurrencyRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/admin/currencies/{id} – 200")
    void update_returns200AndBody() throws Exception {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro Updated", new BigDecimal("1.1"), true);
        CurrencyResponseDTO updated = new CurrencyResponseDTO(1L, "EUR", "Euro Updated", new BigDecimal("1.1"), true);
        when(currencyService.update(eq(1L), any(CurrencyRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v2/admin/currencies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Euro Updated"))
                .andExpect(jsonPath("$.exchangeRateToBase").value(1.1));

        verify(currencyService).update(eq(1L), any(CurrencyRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/v2/admin/currencies/{id} – 200")
    void getById_returns200AndBody() throws Exception {
        when(currencyService.getById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/admin/currencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("EUR"));

        verify(currencyService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/currencies/{id} – 404")
    void getById_notFound_returns404() throws Exception {
        when(currencyService.getById(999L)).thenThrow(new ResourceNotFoundException("Currency not found"));

        mockMvc.perform(get("/api/v2/admin/currencies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(currencyService).getById(999L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/currencies – 200, pagină")
    void getAll_returns200AndPage() throws Exception {
        when(currencyService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/admin/currencies?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value("EUR"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(currencyService).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/v2/admin/currencies – 400 la ValidationException")
    void create_validationException_returns400() throws Exception {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", new BigDecimal("1.0"), true);
        when(currencyService.create(any(CurrencyRequestDTO.class))).thenThrow(new ValidationException("Code already exists"));

        mockMvc.perform(post("/api/v2/admin/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Code already exists"));
    }
}
