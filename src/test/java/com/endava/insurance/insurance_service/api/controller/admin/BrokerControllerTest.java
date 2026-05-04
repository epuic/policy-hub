package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.service.contract.BrokerService;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
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

@WebMvcTest(BrokerController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "ADMIN")
@DisplayName("BrokerController")
class BrokerControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrokerService brokerService;

    private static final BrokerResponseDTO SAMPLE_RESPONSE = new BrokerResponseDTO(
            1L, "BRK1", "Broker One", "b@b.com", "+40123456789",
            BrokerStatus.ACTIVE, new BigDecimal("10")
    );

    @Test
    @DisplayName("POST /api/v2/admin/brokers – 201, returnează broker creat")
    void create_returns201AndBody() throws Exception {
        BrokerCreateDTO request = new BrokerCreateDTO(
                "BRK1", "Broker One", "b@b.com", "+40123456789", "password123",
                BrokerStatus.ACTIVE, new BigDecimal("10")
        );
        when(brokerService.create(any(BrokerCreateDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brokerCode").value("BRK1"))
                .andExpect(jsonPath("$.name").value("Broker One"))
                .andExpect(jsonPath("$.email").value("b@b.com"))
                .andExpect(jsonPath("$.phone").value("+40123456789"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.commissionPercentage").value(10));

        verify(brokerService).create(any(BrokerCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/admin/brokers/{id} – 200, returnează broker actualizat")
    void update_returns200AndBody() throws Exception {
        BrokerUpdateDTO request = new BrokerUpdateDTO("New Name", "new@b.com", "+40223456789", new BigDecimal("8"));
        when(brokerService.update(eq(1L), any(BrokerUpdateDTO.class)))
                .thenReturn(new BrokerResponseDTO(1L, "BRK1", "New Name", "new@b.com", "+40223456789", BrokerStatus.ACTIVE, new BigDecimal("8")));

        mockMvc.perform(put("/api/v2/admin/brokers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@b.com"))
                .andExpect(jsonPath("$.phone").value("+40223456789"));

        verify(brokerService).update(eq(1L), any(BrokerUpdateDTO.class));
    }

    @Test
    @DisplayName("GET /api/v2/admin/brokers/{id} – 200, returnează broker")
    void getById_returns200AndBody() throws Exception {
        when(brokerService.getById(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(get("/api/v2/admin/brokers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brokerCode").value("BRK1"));

        verify(brokerService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/brokers/{id} – 404 când inexistent")
    void getById_notFound_returns404() throws Exception {
        when(brokerService.getById(999L)).thenThrow(new ResourceNotFoundException("Broker not found"));

        mockMvc.perform(get("/api/v2/admin/brokers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Broker not found"));

        verify(brokerService).getById(999L);
    }

    @Test
    @DisplayName("GET /api/v2/admin/brokers – 200, pagină")
    void getAll_returns200AndPage() throws Exception {
        when(brokerService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/v2/admin/brokers?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].brokerCode").value("BRK1"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(brokerService).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/v2/admin/brokers/{id}/activate – 200")
    void activate_returns200() throws Exception {
        when(brokerService.activate(1L)).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/v2/admin/brokers/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(brokerService).activate(1L);
    }

    @Test
    @DisplayName("POST /api/v2/admin/brokers/{id}/deactivate – 200")
    void deactivate_returns200() throws Exception {
        BrokerResponseDTO inactive = new BrokerResponseDTO(1L, "BRK1", "Broker One", "b@b.com", "+40123456789", BrokerStatus.INACTIVE, new BigDecimal("10"));
        when(brokerService.deactivate(1L)).thenReturn(inactive);

        mockMvc.perform(post("/api/v2/admin/brokers/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(brokerService).deactivate(1L);
    }

    @Test
    @DisplayName("POST /api/v2/admin/brokers – 400 când validare eșuează (service ValidationException)")
    void create_validationException_returns400() throws Exception {
        BrokerCreateDTO request = new BrokerCreateDTO("BRK1", "Broker One", "b@b.com", "+40123456789", "password123", BrokerStatus.ACTIVE, new BigDecimal("10"));
        when(brokerService.create(any(BrokerCreateDTO.class))).thenThrow(new ValidationException("Broker code already exists"));

        mockMvc.perform(post("/api/v2/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Broker code already exists"));
    }
}
