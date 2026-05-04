package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.api.controller.BaseControllerTest;
import com.endava.insurance.insurance_service.api.exception.GlobalExceptionHandler;
import com.endava.insurance.insurance_service.config.TestSecurityConfig;
import com.endava.insurance.insurance_service.application.dto.geography.CityWithBuildingsDTO;
import com.endava.insurance.insurance_service.application.dto.geography.GeographyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.GeographyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeographyController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WithMockUser(roles = "BROKER")
@DisplayName("GeographyController")
class GeographyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeographyService geographyService;

    private static final GeographyResponseDTO COUNTRY = new GeographyResponseDTO(1L, "Romania");
    private static final GeographyResponseDTO COUNTY = new GeographyResponseDTO(2L, "Bucuresti");
    private static final CityWithBuildingsDTO CITY = new CityWithBuildingsDTO(
            3L, "Bucuresti Sector 1", new PageImpl<>(List.of())
    );

    @Test
    @DisplayName("GET /api/brokers/countries – 200, pagină țări")
    void getCountries_returns200AndPage() throws Exception {
        when(geographyService.getAllCountries(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(COUNTRY)));

        mockMvc.perform(get("/api/brokers/countries?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Romania"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(geographyService).getAllCountries(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/countries/{countryId}/counties – 200, pagină județe")
    void getCounties_returns200AndPage() throws Exception {
        when(geographyService.getCountiesByCountry(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(COUNTY)));

        mockMvc.perform(get("/api/brokers/countries/1/counties?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Bucuresti"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(geographyService).getCountiesByCountry(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/brokers/counties/{countyId}/cities – 200, pagină orașe cu clădiri")
    void getCities_returns200AndPage() throws Exception {
        when(geographyService.getCitiesByCounty(eq(2L), any(Pageable.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(CITY)));

        mockMvc.perform(get("/api/brokers/counties/2/cities?page=0&size=20&buildingPage=1&buildingSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[0].name").value("Bucuresti Sector 1"))
                .andExpect(jsonPath("$.content[0].buildings").exists())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(geographyService).getCitiesByCounty(eq(2L), any(Pageable.class), any(Pageable.class));
    }
}
