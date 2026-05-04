package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByBrokerDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCityDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountyDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountryDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.application.service.contract.PolicyReportService;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PolicyReportService policyReportService;

    @GetMapping("/policies-by-country")
    public ResponseEntity<List<PolicyReportByCountryDTO>> getPoliciesByCountry(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BuildingType buildingType) throws ValidationException {
        PolicyReportFilter filter = new PolicyReportFilter(from, to, status, currencyCode, buildingType);
        return ResponseEntity.ok(policyReportService.getReportByCountry(filter));
    }

    @GetMapping("/policies-by-county")
    public ResponseEntity<List<PolicyReportByCountyDTO>> getPoliciesByCounty(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BuildingType buildingType) throws ValidationException {
        PolicyReportFilter filter = new PolicyReportFilter(from, to, status, currencyCode, buildingType);
        return ResponseEntity.ok(policyReportService.getReportByCounty(filter));
    }

    @GetMapping("/policies-by-city")
    public ResponseEntity<List<PolicyReportByCityDTO>> getPoliciesByCity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BuildingType buildingType) throws ValidationException {
        PolicyReportFilter filter = new PolicyReportFilter(from, to, status, currencyCode, buildingType);
        return ResponseEntity.ok(policyReportService.getReportByCity(filter));
    }

    @GetMapping("/policies-by-broker")
    public ResponseEntity<List<PolicyReportByBrokerDTO>> getPoliciesByBroker(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BuildingType buildingType) throws ValidationException {
        PolicyReportFilter filter = new PolicyReportFilter(from, to, status, currencyCode, buildingType);
        return ResponseEntity.ok(policyReportService.getReportByBroker(filter));
    }
}
