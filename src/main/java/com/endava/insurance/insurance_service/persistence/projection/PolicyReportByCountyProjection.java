package com.endava.insurance.insurance_service.persistence.projection;

import java.math.BigDecimal;

public interface PolicyReportByCountyProjection {

    String getCountryName();

    String getCountyName();

    String getCurrencyCode();

    Long getPolicyCount();

    BigDecimal getTotalPremium();

    BigDecimal getTotalInBase();
}
