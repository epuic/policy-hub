package com.endava.insurance.insurance_service.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OperationCustomizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SwaggerConfig")
class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    @DisplayName("customOpenAPI bean provides OpenAPI with title and version")
    void openApiBean_hasTitleAndVersion() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Insurance Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("API pentru endpointuri");
    }

    @Test
    @DisplayName("customize bean provides OperationCustomizer")
    void operationCustomizerBean_isPresent() {
        OperationCustomizer customizer = swaggerConfig.customize();
        assertThat(customizer).isNotNull();
    }
}
