package com.tretton37.ranking.elo.application.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApiConfig() {
        String securityScheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Elo Ranking Matchmaking management API")
                        .version("1.0.0")
                        .description("API for ranking games and playerIds")
                )
                .components(new Components()
                        .addSecuritySchemes(securityScheme, new SecurityScheme()
                                .name(securityScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securityScheme));
    }

    @Bean
    public GroupedOpenApi gameApi() {
        return GroupedOpenApi.builder()
                .group("Game API")
                .pathsToMatch("/game/**")
                .build();
    }

    @Bean
    public GroupedOpenApi playerApi() {
        return GroupedOpenApi.builder()
                .group("Player API")
                .pathsToMatch("/player/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tournamentApi() {
        return GroupedOpenApi.builder()
                .group("Tournament API")
                .pathsToMatch("/tournament/**")
                .build();
    }

    @Bean
    public GroupedOpenApi achievementApi() {
        return GroupedOpenApi.builder()
                .group("Achievement API")
                .pathsToMatch("/achievement/**")
                .build();
    }
}
