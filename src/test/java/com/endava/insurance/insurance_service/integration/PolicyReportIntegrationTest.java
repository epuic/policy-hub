package com.endava.insurance.insurance_service.integration;

import com.endava.insurance.insurance_service.persistence.repository.*;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
@DisplayName("Integration: Policy reports")
class PolicyReportIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository brokerAuthRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private CityRepository cityRepository;

    private Long cityId1;
    private Long cityId2;
    private Long currencyId;
    private Long brokerId1;
    private Long brokerId2;
    private LocalDate policyStartDate;
    private LocalDate policyEndDate;
    private int clientCounter;

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
        clientCounter = 0;

        Country country = new Country();
        country.setName("Romania");
        country = countryRepository.save(country);

        County county1 = new County();
        county1.setName("Bucuresti");
        county1.setCountry(country);
        county1 = countyRepository.save(county1);

        County county2 = new County();
        county2.setName("Cluj");
        county2.setCountry(country);
        county2 = countyRepository.save(county2);

        City city1 = new City();
        city1.setName("Bucuresti Sector 1");
        city1.setCounty(county1);
        city1 = cityRepository.save(city1);
        cityId1 = city1.getId();

        City city2 = new City();
        city2.setName("Cluj-Napoca");
        city2.setCounty(county2);
        city2 = cityRepository.save(city2);
        cityId2 = city2.getId();

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

        ResponseEntity<Map> broker1Response = restTemplate.exchange(
                "/api/v2/admin/brokers",
                HttpMethod.POST,
                json(Map.of(
                        "brokerCode", "BRK001",
                        "name", "Broker One",
                        "email", "b1@test.com",
                        "phone", "+40123456789",
                        "password", "password123",
                        "status", "ACTIVE",
                        "commissionPercentage", BigDecimal.valueOf(5)
                )),
                Map.class
        );
        brokerId1 = longFrom(broker1Response.getBody().get("id"));

        ResponseEntity<Map> broker2Response = restTemplate.exchange(
                "/api/v2/admin/brokers",
                HttpMethod.POST,
                json(Map.of(
                        "brokerCode", "BRK002",
                        "name", "Broker Two",
                        "email", "b2@test.com",
                        "phone", "+40987654321",
                        "password", "password123",
                        "status", "ACTIVE",
                        "commissionPercentage", BigDecimal.valueOf(3)
                )),
                Map.class
        );
        brokerId2 = longFrom(broker2Response.getBody().get("id"));

        policyStartDate = LocalDate.now(ZoneOffset.UTC).plusDays(1);
        policyEndDate = policyStartDate.plusYears(1);
    }

    private long[] createClientAndBuilding(Long cityId) {
        String idNumber = "1234567890" + String.format("%03d", ++clientCounter);
        ResponseEntity<Map> clientResp = restTemplate.exchange(
                "/api/brokers/clients",
                HttpMethod.POST,
                json(Map.of(
                        "countryCode", "RO",
                        "type", "INDIVIDUAL",
                        "name", "Client",
                        "identificationNumber", idNumber,
                        "email", "c" + clientCounter + "@x.com",
                        "phone", "071234567" + String.format("%02d", clientCounter),
                        "address", "Addr " + clientCounter
                )),
                Map.class
        );
        assertThat(clientResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(clientResp.getBody()).isNotNull();
        Long clientId = longFrom(clientResp.getBody().get("id"));

        ResponseEntity<Map> buildingResp = restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST,
                json(Map.of(
                        "street", "Str",
                        "number", String.valueOf(clientCounter),
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
        assertThat(buildingResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(buildingResp.getBody()).isNotNull();
        Long buildingId = longFrom(buildingResp.getBody().get("id"));
        return new long[]{clientId, buildingId};
    }

    private void createAndActivatePolicy(long clientId, long buildingId, long brokerId) {
        Map<String, Object> policyRequest = Map.of(
                "clientId", clientId,
                "buildingId", buildingId,
                "brokerId", brokerId,
                "startDate", policyStartDate.toString(),
                "endDate", policyEndDate.toString(),
                "basePremiumAmount", BigDecimal.valueOf(100),
                "currencyId", currencyId
        );
        ResponseEntity<Map> createResp = restTemplate.exchange(
                "/api/v2/brokers/policies",
                HttpMethod.POST,
                json(policyRequest),
                Map.class
        );
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();
        Long policyId = longFrom(createResp.getBody().get("id"));

        ResponseEntity<Map> activateResp = restTemplate.exchange(
                "/api/v2/brokers/policies/" + policyId + "/activate",
                HttpMethod.POST,
                new HttpEntity<>(new HttpHeaders()),
                Map.class
        );
        assertThat(activateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private static Long longFrom(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        return Long.parseLong(value.toString());
    }

    private static long longFromReportMap(Map<String, Object> row, String key) {
        Object v = row != null ? row.get(key) : null;
        return v instanceof Number n ? n.longValue() : (v != null ? Long.parseLong(v.toString()) : 0L);
    }

    @Nested
    @DisplayName("policies-by-country")
    class PoliciesByCountry {

        @Test
        @DisplayName("returns aggregated data by country")
        void returnsAggregatedData() {
            long[] cb = createClientAndBuilding(cityId1);
            createAndActivatePolicy(cb[0], cb[1], brokerId1);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-country?from=" + policyStartDate + "&to=" + policyEndDate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Map> body = response.getBody();
            assertThat(body).isNotNull().hasSizeGreaterThanOrEqualTo(1);

            Map<String, Object> row = body.stream()
                    .filter(r -> "Romania".equals(r.get("countryName")))
                    .findFirst()
                    .orElse(null);
            assertThat(row).isNotNull();
            assertThat(longFromReportMap(row, "policyCount")).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("policies-by-county")
    class PoliciesByCounty {

        @Test
        @DisplayName("returns aggregated data by county")
        void returnsAggregatedData() {
            long[] cb = createClientAndBuilding(cityId1);
            createAndActivatePolicy(cb[0], cb[1], brokerId1);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-county?from=" + policyStartDate + "&to=" + policyEndDate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Map> body = response.getBody();
            assertThat(body).isNotNull().hasSizeGreaterThanOrEqualTo(1);

            Map<String, Object> row = body.stream()
                    .filter(r -> "Bucuresti".equals(r.get("countyName")))
                    .findFirst()
                    .orElse(null);
            assertThat(row).isNotNull();
            assertThat(longFromReportMap(row, "policyCount")).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("policies-by-city")
    class PoliciesByCity {

        @Test
        @DisplayName("returns aggregated data matching seeded policies")
        void returnsAggregatedData() {
            long[] cb1 = createClientAndBuilding(cityId1);
            long[] cb2 = createClientAndBuilding(cityId2);
            long[] cb3 = createClientAndBuilding(cityId1);

            createAndActivatePolicy(cb1[0], cb1[1], brokerId1);
            createAndActivatePolicy(cb2[0], cb2[1], brokerId1);
            createAndActivatePolicy(cb3[0], cb3[1], brokerId2);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-city?from=" + policyStartDate + "&to=" + policyEndDate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Map> body = response.getBody();
            assertThat(body).isNotNull();

            Map<String, Object> bucurestiRow = body.stream()
                    .filter(r -> "Bucuresti Sector 1".equals(r.get("cityName")))
                    .findFirst()
                    .orElse(null);
            assertThat(bucurestiRow)
                    .isNotNull()
                    .containsEntry("countryName", "Romania")
                    .containsEntry("countyName", "Bucuresti");
            assertThat(longFromReportMap(bucurestiRow, "policyCount")).isEqualTo(2L);

            Map<String, Object> clujRow = body.stream()
                    .filter(r -> "Cluj-Napoca".equals(r.get("cityName")))
                    .findFirst()
                    .orElse(null);
            assertThat(clujRow).isNotNull();
            assertThat(longFromReportMap(clujRow, "policyCount")).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("policies-by-broker")
    class PoliciesByBroker {

        @Test
        @DisplayName("returns aggregated data per broker")
        void returnsAggregatedData() {
            long[] cb1 = createClientAndBuilding(cityId1);
            long[] cb2 = createClientAndBuilding(cityId1);
            long[] cb3 = createClientAndBuilding(cityId2);

            createAndActivatePolicy(cb1[0], cb1[1], brokerId1);
            createAndActivatePolicy(cb2[0], cb2[1], brokerId1);
            createAndActivatePolicy(cb3[0], cb3[1], brokerId2);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-broker?from=" + policyStartDate + "&to=" + policyEndDate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Map> body = response.getBody();
            assertThat(body).isNotNull();

            Map<String, Object> broker1Row = body.stream()
                    .filter(r -> "Broker One".equals(r.get("brokerName")))
                    .findFirst()
                    .orElse(null);
            assertThat(broker1Row).isNotNull();
            assertThat(longFromReportMap(broker1Row, "policyCount")).isEqualTo(2L);
            assertThat(broker1Row.get("totalFinalPremium")).isNotNull();

            Map<String, Object> broker2Row = body.stream()
                    .filter(r -> "Broker Two".equals(r.get("brokerName")))
                    .findFirst()
                    .orElse(null);
            assertThat(broker2Row).isNotNull();
            assertThat(longFromReportMap(broker2Row, "policyCount")).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("filters")
    class Filters {

        @Test
        @DisplayName("invalid date range – 400")
        void invalidDateRange_returns400() {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-country?from=2025-12-31&to=2025-01-01",
                    HttpMethod.GET,
                    null,
                    Map.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(String.valueOf(response.getBody().get("message"))).contains("from");
        }

        @Test
        @DisplayName("status filter – applies correctly")
        void statusFilter_applies() {
            long[] cb = createClientAndBuilding(cityId1);
            createAndActivatePolicy(cb[0], cb[1], brokerId1);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    "/api/v2/admin/reports/policies-by-broker?from=" + policyStartDate + "&to=" + policyEndDate + "&status=ACTIVE",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }
}
