package com.jala.university.api.infrastructure.persistence.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * SwaggerConfig is a configuration class for setting up Swagger in the application.
 * It defines the OpenAPI specification and configures the security settings
 * for using JWT (Bearer token) in the API documentation.
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig {

  /**
   * Configures the OpenAPI specification for Swagger documentation.
   * It includes the title, version, and description of the API, and sets up
   * the security scheme for using JWT authentication with the Bearer token.
   *
   * @return the configured OpenAPI instance with security settings for JWT.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    // Define el esquema de seguridad para JWT
    SecurityScheme securityScheme = new SecurityScheme()
    .type(SecurityScheme.Type.HTTP)
    .scheme("bearer")
    .bearerFormat("JWT")
    .in(SecurityScheme.In.HEADER)
    .name("Authorization");

    return new OpenAPI()
    .info(new Info()
    .title("User API")
    .version("1.0")
    .description("API for user CRUD and authentication"))
    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
    .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
  }
}






