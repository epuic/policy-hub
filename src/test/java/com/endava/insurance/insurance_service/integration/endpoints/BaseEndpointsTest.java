package com.endava.insurance.insurance_service.integration.endpoints;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseEndpointsTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected BuildingRepository buildingRepository;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected CountryRepository countryRepository;

    @Autowired
    protected CountyRepository countyRepository;

    @Autowired
    protected CityRepository cityRepository;

    @Autowired
    protected PolicyRepository policyRepository;

    protected Long countryId;
    protected Long countyId;
    protected Long cityId;

    protected HttpEntity<Map<String, Object>> json(Map<String, Object> body) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    protected static Long longFrom(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.valueOf(value.toString());
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
        countryId = country.getId();

        County county = new County();
        county.setName("Bucuresti");
        county.setCountry(country);
        county = countyRepository.save(county);
        countyId = county.getId();

        City city = new City();
        city.setName("Bucuresti Sector 1");
        city.setCounty(county);
        city = cityRepository.save(city);
        cityId = city.getId();
    }
}
