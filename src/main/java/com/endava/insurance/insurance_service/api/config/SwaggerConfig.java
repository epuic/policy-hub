package com.endava.insurance.insurance_service.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    static {
        org.springdoc.core.utils.SpringDocUtils.getConfig()
                .replaceParameterObjectWithClass(org.springframework.data.domain.Pageable.class,
                        org.springdoc.core.converters.models.Pageable.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance Service API")
                        .version("1.0")
                        .description("API pentru endpointuri"));
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getMethodParameters() != null) {
                return operation;
            }
            return operation;
        };
    }
}