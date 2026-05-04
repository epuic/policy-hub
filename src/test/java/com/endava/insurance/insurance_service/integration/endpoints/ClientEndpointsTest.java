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
@DisplayName("Endpoint: /api/brokers/clients")
class ClientEndpointsTest extends BaseEndpointsTest {

    @Test
    @DisplayName("POST /api/brokers/clients – 201, returnează client creat")
    void postClients_createsClient_returns201() {
        Map<String, Object> body = Map.of(
                "countryCode", "RO",
                "type", "INDIVIDUAL",
                "name", "Ion Pop",
                "identificationNumber", "1234567890123",
                "email", "ion@example.com",
                "phone", "+40712345678"
        );

        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/clients",
                HttpMethod.POST,
                json(body),
                Map.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody)
                .containsEntry("countryCode", "RO")
                .containsEntry("name", "Ion Pop")
                .containsEntry("identificationNumber", "1234567890123")
                .containsKey("id");
        assertThat(longFrom(resBody.get("id"))).isPositive();
    }

    @Test
    @DisplayName("PUT /api/brokers/clients/{id} – 200, actualizează clientul")
    void putClientById_updates_returns200() {
        Map<String, Object> create = Map.of(
                "countryCode", "RO", "type", "INDIVIDUAL", "name", "Original",
                "identificationNumber", "1234567890123", "email", "a@b.com",
                "phone", "+40711111111"
        );
        ResponseEntity<Map> cr = restTemplate.exchange("/api/brokers/clients", HttpMethod.POST, json(create), Map.class);
        Long id = longFrom(cr.getBody().get("id"));

        Map<String, Object> update = Map.of(
                "name", "Nume Actualizat", "email", "nou@example.com",
                "phone", "+40722222222", "address", "Adresa noua"
        );

        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/clients/" + id,
                HttpMethod.PUT,
                json(update),
                Map.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody)
                .containsEntry("name", "Nume Actualizat")
                .containsEntry("email", "nou@example.com");
    }

    @Test
    @DisplayName("GET /api/brokers/clients – 200, pagină cu content și totalElements")
    void getClients_returns200AndPage() {
        ResponseEntity<Map> res = restTemplate.getForEntity("/api/brokers/clients?size=5", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        assertThat((List<?>) resBody.get("content")).isEmpty();
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{id} – 200, detaliu client")
    void getClientById_returns200AndDetails() {
        Map<String, Object> create = Map.of(
                "countryCode", "RO", "type", "COMPANY", "name", "Firma SRL",
                "identificationNumber", "12345678", "email", "firma@test.com",
                "phone", "+40733333333"
        );
        ResponseEntity<Map> cr = restTemplate.exchange("/api/brokers/clients", HttpMethod.POST, json(create), Map.class);
        Long id = longFrom(cr.getBody().get("id"));

        ResponseEntity<Map> res = restTemplate.getForEntity("/api/brokers/clients/" + id, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsEntry("name", "Firma SRL");
        assertThat(longFrom(resBody.get("id"))).isEqualTo(id);
    }

    @Test
    @DisplayName("GET /api/brokers/clients/search?name= – 200, pagină filtrată după nume")
    void searchClients_byName_returns200AndPage() {
        Map<String, Object> create = Map.of(
                "countryCode", "RO", "type", "INDIVIDUAL", "name", "Maria Ionescu",
                "identificationNumber", "2990101123456", "email", "m@m.com",
                "phone", "+40744444444"
        );
        restTemplate.exchange("/api/brokers/clients", HttpMethod.POST, json(create), Map.class);

        ResponseEntity<Map> res = restTemplate.getForEntity("/api/brokers/clients/search?name=Maria", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).hasSize(1);
        assertThat((Map<String, Object>) content.get(0)).containsEntry("name", "Maria Ionescu");
    }

    @Test
    @DisplayName("GET /api/brokers/clients/search?identifier= – 200, pagină filtrată după CUI/CNP")
    void searchClients_byIdentifier_returns200AndPage() {
        Map<String, Object> create = Map.of(
                "countryCode", "RO", "type", "COMPANY", "name", "Unique SRL",
                "identificationNumber", "12345678", "email", "u@u.com",
                "phone", "+40755555555"
        );
        restTemplate.exchange("/api/brokers/clients", HttpMethod.POST, json(create), Map.class);

        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/clients/search?identifier=12345678", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).hasSize(1);
        assertThat((Map<String, Object>) content.get(0)).containsEntry("identificationNumber", "12345678");
    }

    @Test
    @DisplayName("GET /api/brokers/clients/{id} – 404 când id inexistent")
    void getClientById_notFound_returns404() {
        ResponseEntity<Map> res = restTemplate.getForEntity("/api/brokers/clients/999999", Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("PUT /api/brokers/clients/{id} – 404 când id inexistent")
    void putClientById_notFound_returns404() {
        Map<String, Object> update = Map.of(
                "name", "Nu exista", "email", "x@y.com",
                "phone", "+40700000000", "address", ""
        );
        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/brokers/clients/999999",
                HttpMethod.PUT,
                json(update),
                Map.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
