package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import com.endava.insurance.insurance_service.application.service.contract.ClientService;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
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

@WebMvcTest(ClientController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "BROKER")
@DisplayName("ClientController")
class ClientControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    private static final ClientResponseDTO SAMPLE_RESPONSE = new ClientResponseDTO(
            1L, "RO", ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
            "ion@example.com", "+40712345678", "Str. X nr. 1"
    );

    @Test
    @DisplayName("POST /api/brokers/clients – 201")
    void createClient_returns201AndBody() throws Exception {
        ClientCreateDTO request = new ClientCreateDTO(
                "RO", ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "+40712345678", null
        );
        when(clientService.createClient(any(ClientCreateDTO.class))).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.countryCode").value("RO"))
                .andExpect(jsonPath("$.type").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.name").value("Ion Pop"))
                .andExpect(jsonPath("$.identificationNumber").value("1234567890123"))
                .andExpect(jsonPath("$.email").value("ion@example.com"))
                .andExpect(jsonPath("$.phone").value("+40712345678"));

        verify(clientService).createClient(any(ClientCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/brokers/clients/{id} – 200")
    void updateClient_returns200AndBody() throws Exception {
        ClientUpdateDTO request = new ClientUpdateDTO("Maria Pop", "maria@example.com", "+40722222222", "Str. Y nr. 2");
        ClientResponseDTO updated = new ClientResponseDTO(
                1L, "RO", ClientType.INDIVIDUAL, "Maria Pop", "1234567890123",
                "maria@example.com", "+40722222222", "Str. Y nr. 2"
        );
        when(clientService.updateClient(eq(1L), any(ClientUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/brokers/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Pop"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.address").value("Str. Y nr. 2"));

        verify(clientService).updateClient(eq(1L), any(ClientUpdateDTO.class));
    }

    @Test
    @DisplayName("GET /api/brokers/clients – 200, pagină")
    void getAllClients_returns200AndPage() throws Exception {
        when(clientService.getAllClients(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/brokers/clients?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Ion Pop"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(clientService).getAllClients(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{id} – 200, detaliu")
    void getClientById_returns200AndDetails() throws Exception {
        ClientDetailsResponseDTO details = new ClientDetailsResponseDTO(
                1L, "RO", ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "+40712345678", "Str. X nr. 1",
                new PageImpl<>(List.of())
        );
        when(clientService.getClientById(eq(1L), any(Pageable.class))).thenReturn(details);

        mockMvc.perform(get("/api/brokers/clients/1?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ion Pop"))
                .andExpect(jsonPath("$.buildings").exists());

        verify(clientService).getClientById(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{id} – 404")
    void getClientById_notFound_returns404() throws Exception {
        when(clientService.getClientById(eq(999L), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/api/brokers/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client not found"));

        verify(clientService).getClientById(eq(999L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/clients/search – 200, pagină filtrată")
    void searchClients_returns200AndPage() throws Exception {
        when(clientService.searchClients(eq("Ion"), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SAMPLE_RESPONSE)));

        mockMvc.perform(get("/api/brokers/clients/search?name=Ion&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Ion Pop"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(clientService).searchClients(eq("Ion"), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/brokers/clients – 400 la ValidationException")
    void createClient_validationException_returns400() throws Exception {
        ClientCreateDTO request = new ClientCreateDTO(
                "RO", ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "+40712345678", null
        );
        when(clientService.createClient(any(ClientCreateDTO.class)))
                .thenThrow(new ValidationException("Identification number already in use"));

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Identification number already in use"));
    }
}
