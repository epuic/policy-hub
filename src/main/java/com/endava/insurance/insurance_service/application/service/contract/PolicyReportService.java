package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByBrokerDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCityDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountryDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountyDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;

import java.util.List;

public interface PolicyReportService {

    List<PolicyReportByCountryDTO> getReportByCountry(PolicyReportFilter filter) throws ValidationException;

    List<PolicyReportByCountyDTO> getReportByCounty(PolicyReportFilter filter) throws ValidationException;

    List<PolicyReportByCityDTO> getReportByCity(PolicyReportFilter filter) throws ValidationException;

    List<PolicyReportByBrokerDTO> getReportByBroker(PolicyReportFilter filter) throws ValidationException;
}
