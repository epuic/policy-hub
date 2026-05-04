package com.endava.insurance.insurance_service.persistence.projection;

import java.math.BigDecimal;

public interface PolicyReportByBrokerProjection {

    String getBrokerName();

    String getCurrencyCode();

    Long getPolicyCount();

    BigDecimal getTotalPremium();

    BigDecimal getTotalInBase();
}
