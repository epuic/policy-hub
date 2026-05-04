package com.endava.insurance.insurance_service.persistence.projection;

import java.math.BigDecimal;

public interface PolicyReportByCountryProjection {

    String getCountryName();

    String getCurrencyCode();

    Long getPolicyCount();

    BigDecimal getTotalPremium();

    BigDecimal getTotalInBase();
}
