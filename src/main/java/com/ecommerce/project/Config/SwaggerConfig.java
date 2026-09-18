package com.ecommerce.project.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI CustomOpenAPI(){


        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Bearer Token");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");


        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", bearerScheme))
                .addSecurityItem(securityRequirement);
    }
}
