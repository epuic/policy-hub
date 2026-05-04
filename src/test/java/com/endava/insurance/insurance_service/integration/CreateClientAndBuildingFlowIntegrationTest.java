package com.endava.insurance.insurance_service.integration;

import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Integration: Create client + building flow")
class CreateClientAndBuildingFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private Long cityId;

    private HttpEntity<Map<String, Object>> json(Map<String, Object> body) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        buildingRepository.deleteAll();
        clientRepository.deleteAll();
        cityRepository.deleteAll();
        countyRepository.deleteAll();
        countryRepository.deleteAll();

        Country country = new Country();
        country.setName("Romania");
        country = countryRepository.save(country);

        County county = new County();
        county.setName("Bucuresti");
        county.setCountry(country);
        county = countyRepository.save(county);

        City city = new City();
        city.setName("Bucuresti Sector 1");
        city.setCounty(county);
        city = cityRepository.save(city);
        cityId = city.getId();
    }

    @Test
    @DisplayName("create client then building – 201, persisted links, GET returns 200")
    void createClientThenBuilding_returnsExpectedStatusAndPersistedLinks() {
        Map<String, Object> clientRequest = Map.of(
                "countryCode", "RO",
                "type", "INDIVIDUAL",
                "name", "Test Client",
                "identificationNumber", "1234567890123",
                "email", "test@example.com",
                "phone", "0712345678",
                "address", "Test Address"
        );

        ResponseEntity<Map> createClientResponse = restTemplate.exchange(
                "/api/brokers/clients",
                HttpMethod.POST,
                json(clientRequest),
                Map.class
        );
        assertThat(createClientResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createClientResponse.getBody()).isNotNull();
        Long clientId = longFrom(createClientResponse.getBody().get("id"));
        assertThat(clientId).isPositive();

        Map<String, Object> buildingRequest = Map.of(
                "street", "Strada Test",
                "number", "1",
                "cityId", cityId,
                "constructionYear", 2000,
                "type", "RESIDENTIAL",
                "numberOfFloors", 2,
                "surfaceArea", 80.0,
                "insuredValue", 100000.0,
                "riskFactorTypes", List.of()
        );

        ResponseEntity<Map> createBuildingResponse = restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST,
                json(buildingRequest),
                Map.class
        );
        assertThat(createBuildingResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createBuildingResponse.getBody()).isNotNull();
        Long buildingId = longFrom(createBuildingResponse.getBody().get("id"));
        assertThat(buildingId).isPositive();
        assertThat(longFrom(createBuildingResponse.getBody().get("clientId"))).isEqualTo(clientId);

        ResponseEntity<Map> getClientResponse = restTemplate.getForEntity(
                "/api/brokers/clients/" + clientId,
                Map.class
        );
        assertThat(getClientResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getClientResponse.getBody()).isNotNull();
        assertThat(longFrom(getClientResponse.getBody().get("id"))).isEqualTo(clientId);
        assertThat(getClientResponse.getBody()).containsEntry("name", "Test Client");

        ResponseEntity<Map> getBuildingResponse = restTemplate.getForEntity(
                "/api/brokers/buildings/" + buildingId,
                Map.class
        );
        assertThat(getBuildingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getBuildingResponse.getBody()).isNotNull();
        assertThat(longFrom(getBuildingResponse.getBody().get("id"))).isEqualTo(buildingId);
        assertThat(longFrom(getBuildingResponse.getBody().get("clientId"))).isEqualTo(clientId);
    }

    private static Long longFrom(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
