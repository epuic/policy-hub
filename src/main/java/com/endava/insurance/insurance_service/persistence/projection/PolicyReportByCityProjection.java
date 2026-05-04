package com.endava.insurance.insurance_service.persistence.projection;

import java.math.BigDecimal;

public interface PolicyReportByCityProjection {

    String getCountryName();

    String getCountyName();

    String getCityName();

    String getCurrencyCode();

    Long getPolicyCount();

    BigDecimal getTotalPremium();

    BigDecimal getTotalInBase();
}
