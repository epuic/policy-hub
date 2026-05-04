package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByBrokerDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCityDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountyDTO;
import com.endava.insurance.insurance_service.application.dto.report.PolicyReportByCountryDTO;
import com.endava.insurance.insurance_service.application.service.contract.PolicyReportService;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "ADMIN")
@DisplayName("ReportController")
class ReportControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyReportService policyReportService;

    private static final List<PolicyReportByCountryDTO> SAMPLE_BY_COUNTRY = List.of(
            new PolicyReportByCountryDTO("Romania", "EUR", 5L, new BigDecimal("500.00"), new BigDecimal("100.00"))
    );

    @Nested
    @DisplayName("GET /api/v2/admin/reports/policies-by-country")
    class PoliciesByCountry {

        @Test
        @DisplayName("without params – 200, returns list")
        void withoutParams_returns200AndList() throws Exception {
            when(policyReportService.getReportByCountry(any())).thenReturn(SAMPLE_BY_COUNTRY);

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-country"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].countryName").value("Romania"))
                    .andExpect(jsonPath("$[0].currencyCode").value("EUR"))
                    .andExpect(jsonPath("$[0].policyCount").value(5))
                    .andExpect(jsonPath("$[0].totalFinalPremium").value(500.00))
                    .andExpect(jsonPath("$[0].totalFinalPremiumInBaseCurrency").value(100.00));

            verify(policyReportService).getReportByCountry(any());
        }

        @Test
        @DisplayName("with filters – 200")
        void withFilters_returns200() throws Exception {
            when(policyReportService.getReportByCountry(any())).thenReturn(SAMPLE_BY_COUNTRY);

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-country")
                            .param("from", "2025-01-01")
                            .param("to", "2025-12-31")
                            .param("status", "ACTIVE")
                            .param("currencyCode", "RON")
                            .param("buildingType", "RESIDENTIAL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            verify(policyReportService).getReportByCountry(any());
        }

        @Test
        @DisplayName("validation error – 400")
        void validationError_returns400() throws Exception {
            when(policyReportService.getReportByCountry(any()))
                    .thenThrow(new ValidationException("Date range invalid: 'from' must be on or before 'to'"));

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-country")
                            .param("from", "2025-12-31")
                            .param("to", "2025-01-01"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation Error"))
                    .andExpect(jsonPath("$.message").value("Date range invalid: 'from' must be on or before 'to'"));

            verify(policyReportService).getReportByCountry(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v2/admin/reports/policies-by-county")
    class PoliciesByCounty {

        @Test
        @DisplayName("returns 200 and list")
        void returns200AndList() throws Exception {
            List<PolicyReportByCountyDTO> sample = List.of(
                    new PolicyReportByCountyDTO("Romania", "Bucuresti", "RON", 2L,
                            new BigDecimal("200.00"), new BigDecimal("200.00"))
            );
            when(policyReportService.getReportByCounty(any())).thenReturn(sample);

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-county"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].countryName").value("Romania"))
                    .andExpect(jsonPath("$[0].countyName").value("Bucuresti"))
                    .andExpect(jsonPath("$[0].policyCount").value(2));

            verify(policyReportService).getReportByCounty(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v2/admin/reports/policies-by-city")
    class PoliciesByCity {

        @Test
        @DisplayName("returns 200 and list")
        void returns200AndList() throws Exception {
            List<PolicyReportByCityDTO> sample = List.of(
                    new PolicyReportByCityDTO("Romania", "Bucuresti", "Sector 1", "EUR", 1L,
                            new BigDecimal("120.00"), new BigDecimal("24.12"))
            );
            when(policyReportService.getReportByCity(any())).thenReturn(sample);

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-city"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].cityName").value("Sector 1"))
                    .andExpect(jsonPath("$[0].policyCount").value(1));

            verify(policyReportService).getReportByCity(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v2/admin/reports/policies-by-broker")
    class PoliciesByBroker {

        @Test
        @DisplayName("returns 200 and list")
        void returns200AndList() throws Exception {
            List<PolicyReportByBrokerDTO> sample = List.of(
                    new PolicyReportByBrokerDTO("Broker One", "RON", 3L,
                            new BigDecimal("300.00"), new BigDecimal("300.00"))
            );
            when(policyReportService.getReportByBroker(any())).thenReturn(sample);

            mockMvc.perform(get("/api/v2/admin/reports/policies-by-broker"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].brokerName").value("Broker One"))
                    .andExpect(jsonPath("$[0].policyCount").value(3));

            verify(policyReportService).getReportByBroker(any());
        }
    }
}
