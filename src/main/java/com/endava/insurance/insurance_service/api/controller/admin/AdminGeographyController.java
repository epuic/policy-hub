package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.geography.CityWithBuildingsDTO;
import com.endava.insurance.insurance_service.application.dto.geography.GeographyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.GeographyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin/geography")
@RequiredArgsConstructor
public class AdminGeographyController {

    private final GeographyService geographyService;

    @GetMapping("/countries")
    public ResponseEntity<Page<GeographyResponseDTO>> getCountries(
            @PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(geographyService.getAllCountries(pageable));
    }

    @GetMapping("/countries/{countryId}/counties")
    public ResponseEntity<Page<GeographyResponseDTO>> getCounties(
            @PathVariable Long countryId,
            @PageableDefault(size = 200) Pageable pageable) {
        return ResponseEntity.ok(geographyService.getCountiesByCountry(countryId, pageable));
    }

    @GetMapping("/counties/{countyId}/cities")
    public ResponseEntity<Page<CityWithBuildingsDTO>> getCities(
            @PathVariable Long countyId,
            @PageableDefault(size = 200) Pageable cityPageable,
            @RequestParam(defaultValue = "1") int buildingPage,
            @RequestParam(defaultValue = "10") int buildingSize) {
        Pageable buildingPageable = PageRequest.of(Math.max(0, buildingPage - 1), buildingSize);
        return ResponseEntity.ok(geographyService.getCitiesByCounty(countyId, cityPageable, buildingPageable));
    }
}
