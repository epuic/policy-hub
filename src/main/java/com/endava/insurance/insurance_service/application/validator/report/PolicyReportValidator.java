package com.endava.insurance.insurance_service.application.validator.report;

import com.endava.insurance.insurance_service.application.dto.report.PolicyReportFilter;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PolicyReportValidator {

    public void validate(PolicyReportFilter filter) throws ValidationException {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new ValidationException("Date range invalid: 'from' must be on or before 'to'");
        }
    }
}
