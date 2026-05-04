package com.endava.insurance.insurance_service.integration.endpoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
@DisplayName("Endpoint: /api/brokers – geography (countries, counties, cities)")
class GeographyEndpointsTest extends BaseEndpointsTest {

    @Test
    @DisplayName("GET /api/brokers/countries – 200, pagină țări")
    void getCountries_returns200AndPage() {
        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/countries?size=20", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).isNotEmpty();
        assertThat((Map<String, Object>) content.get(0)).containsEntry("name", "Romania");
    }

    @Test
    @DisplayName("GET /api/brokers/countries/{countryId}/counties – 200, pagină județe")
    void getCountiesByCountry_returns200AndPage() {
        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/countries/" + countryId + "/counties?size=20", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).hasSize(1);
        assertThat((Map<String, Object>) content.get(0)).containsEntry("name", "Bucuresti");
    }

    @Test
    @DisplayName("GET /api/brokers/counties/{countyId}/cities – 200, pagină orașe cu buildings")
    void getCitiesByCounty_returns200AndPage() {
        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/counties/" + countyId + "/cities?size=20", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        List<?> content = (List<?>) resBody.get("content");
        assertThat(content).hasSize(1);
        Map<String, Object> city = (Map<String, Object>) content.get(0);
        assertThat(city)
                .containsEntry("name", "Bucuresti Sector 1")
                .containsKey("buildings");
    }

    @Test
    @DisplayName("GET /api/brokers/countries/{countryId}/counties – 200, pagină goală când țara inexistentă")
    void getCountiesByCountry_unknownCountry_returnsEmptyPage() {
        ResponseEntity<Map> res = restTemplate.getForEntity(
                "/api/brokers/countries/999999/counties?size=20", Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        Map<String, Object> resBody = (Map<String, Object>) res.getBody();
        assertThat(resBody).containsKeys("content", "totalElements");
        assertThat((List<?>) resBody.get("content")).isEmpty();
    }
}
