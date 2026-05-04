package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.application.mapper.client.ClientMapper;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.application.validator.client.ClientValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl - clients management")
@SuppressWarnings("java:S1130")
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientValidator clientValidator;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private BuildingMapper buildingMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private static final String VALID_PHONE = "+40712345678";
    private static final String COUNTRY_RO = "RO";

    @Test
    @DisplayName("createClient validates, maps, saves and returns DTO")
    void createClient_validRequest_returnsSavedClientDto() throws ValidationException {
        ClientCreateDTO request = new ClientCreateDTO(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "0712345678", null
        );
        Client entity = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "0712345678", null
        );
        Client saved = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "0712345678", null
        );
        ClientResponseDTO dto = new ClientResponseDTO(
                1L, COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "ion@example.com", "0712345678", null
        );
        doNothing().when(clientValidator).validateNewClient(request);
        when(clientMapper.toEntity(request)).thenReturn(entity);
        when(clientRepository.save(any(Client.class))).thenReturn(saved);
        when(clientMapper.toResponse(saved)).thenReturn(dto);

        ClientResponseDTO result = clientService.createClient(request);

        assertThat(result).isEqualTo(dto);
        verify(clientValidator).validateNewClient(request);
        verify(clientMapper).toEntity(request);
        verify(clientRepository).save(any(Client.class));
        verify(clientMapper).toResponse(saved);
    }

    @Test
    @DisplayName("updateClient when client not found throws ResourceNotFoundException")
    void updateClient_notFound_throws() throws ResourceNotFoundException, ValidationException {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());
        ClientUpdateDTO update = new ClientUpdateDTO("New Name", "a@b.com", "0712345678", null);

        assertThatThrownBy(() -> clientService.updateClient(999L, update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found");
        verify(clientValidator, never()).validateClientUpdate(any(), any());
    }

    @Test
    @DisplayName("updateClient when found validates, updates and returns DTO")
    void updateClient_found_validatesAndReturnsDto() throws ResourceNotFoundException, ValidationException {
        Long id = 1L;
        Client client = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Old", "1234567890123",
                "old@example.com", "0712345678", null
        );
        ClientUpdateDTO update = new ClientUpdateDTO("New Name", "new@example.com", "0799999999", "Address");
        Client saved = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "New Name", "1234567890123",
                "new@example.com", "0799999999", "Address"
        );
        ClientResponseDTO dto = new ClientResponseDTO(
                id, COUNTRY_RO, ClientType.INDIVIDUAL, "New Name", "1234567890123",
                "new@example.com", "0799999999", "Address"
        );
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        doNothing().when(clientValidator).validateClientUpdate(client, update);
        doNothing().when(clientMapper).updateEntityFromRequest(update, client);
        when(clientRepository.save(client)).thenReturn(saved);
        when(clientMapper.toResponse(saved)).thenReturn(dto);

        ClientResponseDTO result = clientService.updateClient(id, update);

        assertThat(result).isEqualTo(dto);
        verify(clientMapper).updateEntityFromRequest(update, client);
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("getClientById when not found throws ResourceNotFoundException")
    void getClientById_notFound_throws() throws ResourceNotFoundException {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> clientService.getClientById(999L, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found");
    }

    @Test
    @DisplayName("getClientById when found returns ClientDetailsResponseDTO")
    void getClientById_found_returnsDetails() throws ResourceNotFoundException, ValidationException {
        Long id = 1L;
        Client client = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", "1234567890123",
                "ion@ex.com", "0712345678", null
        );
        Pageable pageable = PageRequest.of(0, 10);
        ClientDetailsResponseDTO details = new ClientDetailsResponseDTO(
                id, COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", "1234567890123",
                "ion@ex.com", "0712345678", null, Page.empty(pageable)
        );
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(buildingRepository.findByOwnerId(id, pageable)).thenReturn(Page.empty(pageable));
        when(clientMapper.toDetailsResponse(eq(client), any())).thenReturn(details);

        ClientDetailsResponseDTO result = clientService.getClientById(id, pageable);

        assertThat(result).isEqualTo(details);
    }

    @Test
    @DisplayName("getAllClients returns page from repository")
    void getAllClients_returnsMappedPage() throws ValidationException {
        Pageable pageable = PageRequest.of(0, 10);
        Client c = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", "1234567890123",
                "i@x.com", VALID_PHONE, null
        );
        Page<Client> clientPage = new PageImpl<>(List.of(c), pageable, 1);
        ClientResponseDTO dto = new ClientResponseDTO(1L, COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", "1234567890123", "i@x.com", VALID_PHONE, null);
        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toResponse(c)).thenReturn(dto);

        Page<ClientResponseDTO> result = clientService.getAllClients(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Ion");
    }

    @Test
    @DisplayName("searchClients by identifier returns single client when found")
    void searchClients_byIdentifier_found_returnsPageWithOne() throws ValidationException {
        String identifier = "1234567890123";
        Pageable pageable = PageRequest.of(0, 10);
        Client client = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", identifier,
                "i@x.com", VALID_PHONE, null
        );
        ClientResponseDTO dto = new ClientResponseDTO(1L, COUNTRY_RO, ClientType.INDIVIDUAL, "Ion", identifier, "i@x.com", VALID_PHONE, null);
        when(clientRepository.findByIdentificationNumber(identifier)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(dto);

        Page<ClientResponseDTO> result = clientService.searchClients(null, identifier, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).identificationNumber()).isEqualTo(identifier);
    }

    @Test
    @DisplayName("searchClients by identifier not found returns empty page")
    void searchClients_byIdentifier_notFound_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clientRepository.findByIdentificationNumber("9999999999999")).thenReturn(Optional.empty());

        Page<ClientResponseDTO> result = clientService.searchClients(null, "9999999999999", pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("searchClients by name delegates to findByNameContainingIgnoreCase")
    void searchClients_byName_returnsMappedPage() throws ValidationException {
        Pageable pageable = PageRequest.of(0, 10);
        Client c = new Client(
                COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123",
                "i@x.com", VALID_PHONE, null
        );
        when(clientRepository.findByNameContainingIgnoreCase("Ion", pageable))
                .thenReturn(new PageImpl<>(List.of(c), pageable, 1));
        when(clientMapper.toResponse(c)).thenReturn(
                new ClientResponseDTO(1L, COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", "1234567890123", "i@x.com", VALID_PHONE, null)
        );

        Page<ClientResponseDTO> result = clientService.searchClients("Ion", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Ion Pop");
    }
}
