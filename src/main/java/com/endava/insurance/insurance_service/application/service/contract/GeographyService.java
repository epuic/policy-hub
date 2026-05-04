package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.geography.CityWithBuildingsDTO;
import com.endava.insurance.insurance_service.application.dto.geography.GeographyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeographyService {

    Page<GeographyResponseDTO> getAllCountries(Pageable pageable);

    Page<GeographyResponseDTO> getCountiesByCountry(Long countryId, Pageable pageable);

    Page<CityWithBuildingsDTO> getCitiesByCounty(Long countyId, Pageable cityPageable, Pageable buildingPageable);
}