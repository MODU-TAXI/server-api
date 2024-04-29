package com.modutaxi.api.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {@Server(url = "${spring.swagger.domain}")},
    info = @Info(title = "Modu's Taxi Server's API",
        description = "모두의 택시 서버 API 명세서",
        version = "${server-version}"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

  @Value("${server-version}")
  private String version;

  @Bean
  public GroupedOpenApi OpenApi() {
    String[] paths = {"/**"};
    return GroupedOpenApi.builder()
        .group(String.format("모두의 택시 API %s", version))
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public OpenAPI openAPI() {
    // SecuritySecheme명
    String jwtSchemeName = "jwtAuth";
    // API 요청헤더에 인증정보 포함
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
    // SecuritySchemes 등록
    Components components = new Components()
        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer"));

    return new OpenAPI()
        .addSecurityItem(securityRequirement)
        .components(components);
  }
}
