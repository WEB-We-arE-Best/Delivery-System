package com.webest.rider.infrastructure.config;

import static com.webest.rider.common.constant.RiderStaticVariable.DOCS_AUTH_KEY;
import static com.webest.web.common.CommonStaticVariable.SWAGGER_TITLE;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Components components = new Components();

        components.addSecuritySchemes(DOCS_AUTH_KEY, securityScheme());

        return new OpenAPI()
            .addServersItem(new Server().url("/"))
            .info(info())
            .components(components);

    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
            .name(DOCS_AUTH_KEY)
            .type(Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT");
    }

    private Info info() {
        return new Info()
            .title(SWAGGER_TITLE)
            .version("0.0.1");
    }
}

