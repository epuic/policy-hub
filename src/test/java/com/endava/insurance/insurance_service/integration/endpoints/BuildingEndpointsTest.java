package com.endava.insurance.insurance_service.integration.endpoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
@DisplayName("Endpoint: /api/brokers – buildings")
class BuildingEndpointsTest extends BaseEndpointsTest {

    private Map<String, Object> buildingBody() {
        return Map.of(
                "street", "Strada Exemplu",
                "number", "10",
                "cityId", cityId,
                "constructionYear", 2005,
                "type", "RESIDENTIAL",
                "numberOfFloors", 3,
                "surfaceArea", 95.0,
                "insuredValue", 120_000.0,
                "riskFactorTypes", List.of()
        );
    }

    @Test
    @DisplayName("POST /api/brokers/clients/{clientId}/buildings – 201, returnează clădire creată")
    void postBuilding_createsBuilding_returns201() {
        Map<String, Object> client = Map.of(
                "countryCode", "RO", "type", "INDIVIDUAL", "name", "Proprietar",
                "identificationNumber", "1234567890123", "email", "p@ex.com",
                "phone", "+40712345678"
        );
        ResponseEntity<Map> cr = restTemplate.exchange(
                "/api/brokers/clients", HttpMethod.POST, json(client), Map.class);
        Long clientId = longFrom(cr.getBody().get("id"));

        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST,
                json(buildingBody()),
                Map.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody.get("fullAddress")).asString().contains("Strada Exemplu").contains("10");
        assertThat(longFrom(resBody.get("clientId"))).isEqualTo(clientId);
        assertThat(longFrom(resBody.get("id"))).isPositive();
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{clientId}/buildings – 200, pagină cu clădiri")
    void getBuildingsByClient_returns200AndPage() {
        Map<String, Object> client = Map.of(
                "countryCode", "RO", "type", "INDIVIDUAL", "name", "Client Imobiliar",
                "identificationNumber", "1234567890123", "email", "c@ex.com",
                "phone", "+40711111111"
        );
        ResponseEntity<Map> cr = restTemplate.exchange(
                "/api/brokers/clients", HttpMethod.POST, json(client), Map.class);
        Long clientId = longFrom(cr.getBody().get("id"));

        restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST, json(buildingBody()), Map.class);

        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/clients/" + clientId + "/buildings?size=10", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).hasSize(1);
        assertThat((Map<String, Object>) content.get(0)).containsKey("fullAddress");
        assertThat(((Map<String, Object>) content.get(0)).get("fullAddress")).asString().contains("Strada Exemplu");
    }

    @Test
    @DisplayName("GET /api/brokers/buildings/{buildingId} – 200, detaliu clădire")
    void getBuildingById_returns200AndDetails() {
        Map<String, Object> client = Map.of(
                "countryCode", "RO", "type", "COMPANY", "name", "Firma Constructii",
                "identificationNumber", "11111111", "email", "fc@ex.com",
                "phone", "+40722222222"
        );
        ResponseEntity<Map> cr = restTemplate.exchange(
                "/api/brokers/clients", HttpMethod.POST, json(client), Map.class);
        Long clientId = longFrom(cr.getBody().get("id"));

        ResponseEntity<Map> createRes = restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST, json(buildingBody()), Map.class);
        Long buildingId = longFrom(createRes.getBody().get("id"));

        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/buildings/" + buildingId, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(longFrom(resBody.get("id"))).isEqualTo(buildingId);
        assertThat(resBody.get("fullAddress")).asString().contains("Strada Exemplu").contains("10");
    }

    @Test
    @DisplayName("PUT /api/brokers/buildings/{buildingId} – 200, actualizează clădirea")
    void putBuilding_updates_returns200() {
        Map<String, Object> client = Map.of(
                "countryCode", "RO", "type", "INDIVIDUAL", "name", "Owner",
                "identificationNumber", "1234567890123", "email", "o@ex.com",
                "phone", "+40733333333"
        );
        ResponseEntity<Map> cr = restTemplate.exchange(
                "/api/brokers/clients", HttpMethod.POST, json(client), Map.class);
        Long clientId = longFrom(cr.getBody().get("id"));

        ResponseEntity<Map> createRes = restTemplate.exchange(
                "/api/brokers/clients/" + clientId + "/buildings",
                HttpMethod.POST, json(buildingBody()), Map.class);
        Long buildingId = longFrom(createRes.getBody().get("id"));

        Map<String, Object> update = Map.of(
                "street", "Strada Noua",
                "number", "20",
                "cityId", cityId,
                "constructionYear", 2010,
                "type", "OFFICE",
                "numberOfFloors", 5,
                "surfaceArea", 200.0,
                "insuredValue", 300_000.0,
                "riskFactorTypes", List.of()
        );

        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/buildings/" + buildingId,
                HttpMethod.PUT,
                json(update),
                Map.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody.get("fullAddress")).asString().contains("Strada Noua").contains("20");
    }

    @Test
    @DisplayName("GET /api/brokers/buildings/{buildingId} – 404 când id inexistent")
    void getBuildingById_notFound_returns404() {
        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/buildings/999999", Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /api/brokers/clients/{clientId}/buildings – 404 când client inexistent")
    void postBuilding_clientNotFound_returns404() {
        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/clients/999999/buildings",
                HttpMethod.POST,
                json(buildingBody()),
                Map.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("PUT /api/brokers/buildings/{buildingId} – 404 când id inexistent")
    void putBuilding_notFound_returns404() {
        Map<String, Object> update = Map.of(
                "street", "Strada", "number", "1", "cityId", cityId,
                "constructionYear", 2000, "type", "RESIDENTIAL",
                "numberOfFloors", 1, "surfaceArea", 50.0, "insuredValue", 50_000.0,
                "riskFactorTypes", List.of()
        );
        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/buildings/999999",
                HttpMethod.PUT,
                json(update),
                Map.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
