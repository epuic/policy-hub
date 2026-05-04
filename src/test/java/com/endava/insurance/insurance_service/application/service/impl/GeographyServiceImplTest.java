package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.geography.CityWithBuildingsDTO;
import com.endava.insurance.insurance_service.application.dto.geography.GeographyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.Country;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeographyServiceImpl")
class GeographyServiceImplTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountyRepository countyRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private BuildingMapper buildingMapper;

    @InjectMocks
    private GeographyServiceImpl geographyService;

    @Test
    @DisplayName("getAllCountries returns page of GeographyResponseDTO")
    void getAllCountries_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Country ro = new Country();
        ro.setId(1L);
        ro.setName("Romania");
        Page<Country> countryPage = new PageImpl<>(List.of(ro), pageable, 1);
        when(countryRepository.findAll(pageable)).thenReturn(countryPage);

        Page<GeographyResponseDTO> result = geographyService.getAllCountries(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
        assertThat(result.getContent().get(0).name()).isEqualTo("Romania");
    }

    @Test
    @DisplayName("getCountiesByCountry returns page of GeographyResponseDTO")
    void getCountiesByCountry_returnsMappedPage() {
        Long countryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        County c = new County();
        c.setId(1L);
        c.setName("Bucuresti");
        Page<County> countyPage = new PageImpl<>(List.of(c), pageable, 1);
        when(countyRepository.findByCountryId(countryId, pageable)).thenReturn(countyPage);

        Page<GeographyResponseDTO> result = geographyService.getCountiesByCountry(countryId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
        assertThat(result.getContent().get(0).name()).isEqualTo("Bucuresti");
    }

    @Test
    @DisplayName("getCitiesByCounty returns page of CityWithBuildingsDTO")
    void getCitiesByCounty_returnsMappedPage() {
        Long countyId = 1L;
        Pageable cityPageable = PageRequest.of(0, 10);
        Pageable buildingPageable = PageRequest.of(0, 5);
        City city = new City();
        city.setId(1L);
        city.setName("Sector 1");
        Page<City> cityPage = new PageImpl<>(List.of(city), cityPageable, 1);
        when(cityRepository.findByCountyId(countyId, cityPageable)).thenReturn(cityPage);
        when(buildingRepository.findByCityId(1L, buildingPageable)).thenReturn(Page.empty(buildingPageable));

        Page<CityWithBuildingsDTO> result = geographyService.getCitiesByCounty(countyId, cityPageable, buildingPageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
        assertThat(result.getContent().get(0).name()).isEqualTo("Sector 1");
        assertThat(result.getContent().get(0).buildings().getContent()).isEmpty();
    }
}
