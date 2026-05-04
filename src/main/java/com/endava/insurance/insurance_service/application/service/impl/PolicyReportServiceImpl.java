package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByBrokerDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCityDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountyDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountryDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.application.service.contract.PolicyReportService;
import com.endava.insurance.insurance_service.application.validator.report.PolicyReportValidator;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyReportServiceImpl implements PolicyReportService {

    private final PolicyRepository policyRepository;
    private final PolicyReportValidator reportValidator;

    @Override
    public List<PolicyReportByCountryDTO> getReportByCountry(PolicyReportFilter filter) throws ValidationException {
        reportValidator.validate(filter);
        String status = filter.status() != null ? filter.status().name() : null;
        String buildingType = filter.buildingType() != null ? filter.buildingType().name() : null;
        String fromDate = filter.from() != null ? filter.from().toString() : null;
        String toDate = filter.to() != null ? filter.to().toString() : null;
        List<PolicyReportByCountryDTO> result = policyRepository.findReportByCountry(
                fromDate, toDate, status, filter.currencyCode(), buildingType)
                .stream()
                .map(p -> new PolicyReportByCountryDTO(
                        p.getCountryName(), p.getCurrencyCode(), nullSafeLong(p.getPolicyCount()),
                        p.getTotalPremium(), p.getTotalInBase()))
                .toList();
        log.debug("Report by country: {} rows", result.size());
        return result;
    }

    @Override
    public List<PolicyReportByCountyDTO> getReportByCounty(PolicyReportFilter filter) throws ValidationException {
        reportValidator.validate(filter);
        String status = filter.status() != null ? filter.status().name() : null;
        String buildingType = filter.buildingType() != null ? filter.buildingType().name() : null;
        String fromDate = filter.from() != null ? filter.from().toString() : null;
        String toDate = filter.to() != null ? filter.to().toString() : null;
        List<PolicyReportByCountyDTO> result = policyRepository.findReportByCounty(
                fromDate, toDate, status, filter.currencyCode(), buildingType)
                .stream()
                .map(p -> new PolicyReportByCountyDTO(
                        p.getCountryName(), p.getCountyName(), p.getCurrencyCode(), nullSafeLong(p.getPolicyCount()),
                        p.getTotalPremium(), p.getTotalInBase()))
                .toList();
        log.debug("Report by county: {} rows", result.size());
        return result;
    }

    @Override
    public List<PolicyReportByCityDTO> getReportByCity(PolicyReportFilter filter) throws ValidationException {
        reportValidator.validate(filter);
        String status = filter.status() != null ? filter.status().name() : null;
        String buildingType = filter.buildingType() != null ? filter.buildingType().name() : null;
        String fromDate = filter.from() != null ? filter.from().toString() : null;
        String toDate = filter.to() != null ? filter.to().toString() : null;
        List<PolicyReportByCityDTO> result = policyRepository.findReportByCity(
                fromDate, toDate, status, filter.currencyCode(), buildingType)
                .stream()
                .map(p -> new PolicyReportByCityDTO(
                        p.getCountryName(), p.getCountyName(), p.getCityName(), p.getCurrencyCode(), nullSafeLong(p.getPolicyCount()),
                        p.getTotalPremium(), p.getTotalInBase()))
                .toList();
        log.debug("Report by city: {} rows", result.size());
        return result;
    }

    @Override
    public List<PolicyReportByBrokerDTO> getReportByBroker(PolicyReportFilter filter) throws ValidationException {
        reportValidator.validate(filter);
        String status = filter.status() != null ? filter.status().name() : null;
        String buildingType = filter.buildingType() != null ? filter.buildingType().name() : null;
        String fromDate = filter.from() != null ? filter.from().toString() : null;
        String toDate = filter.to() != null ? filter.to().toString() : null;
        List<PolicyReportByBrokerDTO> result = policyRepository.findReportByBroker(
                fromDate, toDate, status, filter.currencyCode(), buildingType)
                .stream()
                .map(p -> new PolicyReportByBrokerDTO(
                        p.getBrokerName(), p.getCurrencyCode(), nullSafeLong(p.getPolicyCount()),
                        p.getTotalPremium(), p.getTotalInBase()))
                .toList();
        log.debug("Report by broker: {} rows", result.size());
        return result;
    }

    private static long nullSafeLong(Long value) {
        return value != null ? value : 0L;
    }
}
