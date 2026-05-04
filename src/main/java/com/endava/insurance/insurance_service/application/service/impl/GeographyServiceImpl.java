package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.geography.CityWithBuildingsDTO;
import com.endava.insurance.insurance_service.application.dto.geography.GeographyResponseDTO;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.CityRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountryRepository;
import com.endava.insurance.insurance_service.persistence.repository.CountyRepository;
import com.endava.insurance.insurance_service.application.service.contract.GeographyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeographyServiceImpl implements GeographyService {

    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    public Page<GeographyResponseDTO> getAllCountries(Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(c -> new GeographyResponseDTO(c.getId(), c.getName()));
    }

    @Override
    public Page<GeographyResponseDTO> getCountiesByCountry(Long countryId, Pageable pageable) {
        return countyRepository.findByCountryId(countryId, pageable)
                .map(c -> new GeographyResponseDTO(c.getId(), c.getName()));
    }

    @Override
    public Page<CityWithBuildingsDTO> getCitiesByCounty(Long countyId, Pageable cityPageable, Pageable buildingPageable) {
        return cityRepository.findByCountyId(countyId, cityPageable)
                .map(city -> {
                    Page<BuildingResponseDTO> buildings = buildingRepository.findByCityId(city.getId(), buildingPageable)
                            .map(buildingMapper::toResponse);
                    return new CityWithBuildingsDTO(city.getId(), city.getName(), buildings);
                });
    }
}