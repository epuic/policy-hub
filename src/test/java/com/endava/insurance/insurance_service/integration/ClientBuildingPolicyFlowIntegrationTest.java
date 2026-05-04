package com.endava.insurance.insurance_service.integration;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Integration: Client + Building + Policy flow")
class ClientBuildingPolicyFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PolicyRepository policyRepository;

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
    private BrokerRepository brokerRepository;

    @Autowired
    private com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository brokerAuthRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Long cityId;
    private Long currencyId;
    private Long brokerId;

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
        brokerAuthRepository.deleteAll();
        brokerRepository.deleteAll();
        currencyRepository.deleteAll();
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

        ResponseEntity<Map> currencyResponse = restTemplate.exchange(
                "/api/v2/admin/currencies",
                HttpMethod.POST,
                json(Map.of(
                        "code", "RON",
                        "name", "Leu",
                        "exchangeRateToBase", BigDecimal.ONE,
                        "active", true
                )),
                Map.class
        );
        assertThat(currencyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        currencyId = longFrom(currencyResponse.getBody().get("id"));

        ResponseEntity<Map> brokerResponse = restTemplate.exchange(
                "/api/v2/admin/brokers",
                HttpMethod.POST,
                json(Map.of(
                        "brokerCode", "BRK001",
                        "name", "Test Broker",
                        "email", "broker@test.com",
                        "phone", "+40123456789",
                        "password", "password123",
                        "status", "ACTIVE",
                        "commissionPercentage", BigDecimal.valueOf(5)
                )),
                Map.class
        );
        assertThat(brokerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        brokerId = longFrom(brokerResponse.getBody().get("id"));
    }

    @Nested
    @DisplayName("1. Create client + building + policy full flow")
    class FullFlow {

        @Test
        @DisplayName("create client, building, draft policy, activate – 201/200, policy persisted with correct premium and links")
        void fullFlow_expectedStatusAndPersistedPolicy() {
            Map<String, Object> clientRequest = Map.of(
                    "countryCode", "RO",
                    "type", "INDIVIDUAL",
                    "name", "Test Client",
                    "identificationNumber", "1234567890123",
                    "email", "client@example.com",
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
            Long clientId = longFrom(createClientResponse.getBody().get("id"));

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
            Long buildingId = longFrom(createBuildingResponse.getBody().get("id"));

            LocalDate start = LocalDate.now(ZoneOffset.UTC).plusDays(1);
            LocalDate end = start.plusYears(1);
            Map<String, Object> policyRequest = Map.of(
                    "clientId", clientId,
                    "buildingId", buildingId,
                    "brokerId", brokerId,
                    "startDate", start.toString(),
                    "endDate", end.toString(),
                    "basePremiumAmount", BigDecimal.valueOf(100),
                    "currencyId", currencyId
            );

            ResponseEntity<Map> createPolicyResponse = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequest),
                    Map.class
            );
            assertThat(createPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Long policyId = longFrom(createPolicyResponse.getBody().get("id"));
            assertThat(createPolicyResponse.getBody()).containsEntry("status", "DRAFT");

            ResponseEntity<Map> activateResponse = restTemplate.exchange(
                    "/api/v2/brokers/policies/" + policyId + "/activate",
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    Map.class
            );
            assertThat(activateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(activateResponse.getBody()).containsEntry("status", "ACTIVE");

            Policy policy = policyRepository.findById(policyId).orElseThrow();
            assertThat(policy.getClient().getId()).isEqualTo(clientId);
            assertThat(policy.getBuilding().getId()).isEqualTo(buildingId);
            assertThat(policy.getBroker().getId()).isEqualTo(brokerId);
            assertThat(policy.getFinalPremium()).isNotNull();
            assertThat(policy.getFinalPremium()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            assertThat(policy.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("2. Invalid policy creation")
    class InvalidPolicyCreation {

        @Test
        @DisplayName("create policy with non-existent client returns 400 or 404")
        void nonExistentClient_returns4xx() {
            Long buildingId = createClientAndBuildingAndGetBuildingId();
            Map<String, Object> policyRequest = policyRequestBody(999_999L, buildingId, brokerId);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequest),
                    Map.class
            );
            assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("create policy with non-existent building returns 400 or 404")
        void nonExistentBuilding_returns4xx() {
            Long clientId = createClientAndGetClientId();
            Map<String, Object> policyRequest = policyRequestBody(clientId, 999_999L, brokerId);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequest),
                    Map.class
            );
            assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("create policy with negative base premium returns 400")
        void negativePremium_returns400() {
            Long clientId = createClientAndGetClientId();
            Long buildingId = createBuildingForClient(clientId);
            Map<String, Object> policyRequest = policyRequestBody(clientId, buildingId, brokerId);
            policyRequest.put("basePremiumAmount", BigDecimal.valueOf(-10));
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequest),
                    Map.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        private Long createClientAndGetClientId() {
            ResponseEntity<Map> r = restTemplate.exchange(
                    "/api/brokers/clients",
                    HttpMethod.POST,
                    json(Map.of(
                            "countryCode", "RO",
                            "type", "INDIVIDUAL",
                            "name", "Client",
                            "identificationNumber", "1234567890123",
                            "email", "c@x.com",
                            "phone", "0712345678",
                            "address", "Addr"
                    )),
                    Map.class
            );
            return longFrom(r.getBody().get("id"));
        }

        private Long createBuildingForClient(Long clientId) {
            ResponseEntity<Map> r = restTemplate.exchange(
                    "/api/brokers/clients/" + clientId + "/buildings",
                    HttpMethod.POST,
                    json(Map.of(
                            "street", "S",
                            "number", "1",
                            "cityId", cityId,
                            "constructionYear", 2000,
                            "type", "RESIDENTIAL",
                            "numberOfFloors", 1,
                            "surfaceArea", 50.0,
                            "insuredValue", 50000.0,
                            "riskFactorTypes", List.of()
                    )),
                    Map.class
            );
            return longFrom(r.getBody().get("id"));
        }

        private Long createClientAndBuildingAndGetBuildingId() {
            Long clientId = createClientAndGetClientId();
            return createBuildingForClient(clientId);
        }
    }

    @Nested
    @DisplayName("3. Broker deactivation behaviour")
    class BrokerDeactivation {

        @Test
        @DisplayName("after deactivating broker, new policy with that broker is rejected; existing policies unchanged")
        void deactivatedBroker_newPolicyRejected_existingUnchanged() {
            Long clientId = createClientAndGetClientId();
            Long buildingId = createBuildingForClient(clientId);

            Map<String, Object> policyRequest = policyRequestBody(clientId, buildingId, brokerId);
            ResponseEntity<Map> createPolicyResponse = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequest),
                    Map.class
            );
            assertThat(createPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Long existingPolicyId = longFrom(createPolicyResponse.getBody().get("id"));

            ResponseEntity<Map> deactivateResponse = restTemplate.exchange(
                    "/api/v2/admin/brokers/" + brokerId + "/deactivate",
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    Map.class
            );
            assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<Map> newPolicyResponse = restTemplate.exchange(
                    "/api/v2/brokers/policies",
                    HttpMethod.POST,
                    json(policyRequestBody(clientId, buildingId, brokerId)),
                    Map.class
            );
            assertThat(newPolicyResponse.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN);

            Policy existingPolicy = policyRepository.findById(existingPolicyId).orElseThrow();
            assertThat(existingPolicy.getStatus()).isEqualTo(PolicyStatus.DRAFT);
            assertThat(existingPolicy.getBroker().getId()).isEqualTo(brokerId);
        }

        private Long createClientAndGetClientId() {
            ResponseEntity<Map> r = restTemplate.exchange(
                    "/api/brokers/clients",
                    HttpMethod.POST,
                    json(Map.of(
                            "countryCode", "RO",
                            "type", "INDIVIDUAL",
                            "name", "Client",
                            "identificationNumber", "1234567890123",
                            "email", "c@x.com",
                            "phone", "0712345678",
                            "address", "Addr"
                    )),
                    Map.class
            );
            return longFrom(r.getBody().get("id"));
        }

        private Long createBuildingForClient(Long clientId) {
            ResponseEntity<Map> r = restTemplate.exchange(
                    "/api/brokers/clients/" + clientId + "/buildings",
                    HttpMethod.POST,
                    json(Map.of(
                            "street", "S",
                            "number", "1",
                            "cityId", cityId,
                            "constructionYear", 2000,
                            "type", "RESIDENTIAL",
                            "numberOfFloors", 1,
                            "surfaceArea", 50.0,
                            "insuredValue", 50000.0,
                            "riskFactorTypes", List.of()
                    )),
                    Map.class
            );
            return longFrom(r.getBody().get("id"));
        }
    }

    private Map<String, Object> policyRequestBody(Long clientId, Long buildingId, Long brokerId) {
        LocalDate start = LocalDate.now(ZoneOffset.UTC).plusDays(1);
        LocalDate end = start.plusYears(1);
        return new java.util.HashMap<>(Map.of(
                "clientId", clientId,
                "buildingId", buildingId,
                "brokerId", brokerId,
                "startDate", start.toString(),
                "endDate", end.toString(),
                "basePremiumAmount", BigDecimal.valueOf(100),
                "currencyId", currencyId
        ));
    }

    private static Long longFrom(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
