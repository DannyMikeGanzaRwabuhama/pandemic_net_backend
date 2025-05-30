package com.app.pandemicnet.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * Configuration for OpenAPI (Swagger) documentation.
 * Customizes the API documentation and UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String API_VERSION = "1.0.0";
    private static final String API_TITLE = "PandemicNet API";
    private static final String API_DESCRIPTION = "REST API for PandemicNet - A contact tracing and health monitoring system";

    /**
     * Configures the OpenAPI documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(getServers())
                .info(buildInfo())
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .security(Collections.singletonList(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)))
                .externalDocs(new ExternalDocumentation()
                        .description("PandemicNet Documentation")
                        .url("https://github.com/your-org/pandemicnet/docs"));
    }

    /**
     * Builds the API information section.
     *
     * @return Info object with API metadata
     */
    private Info buildInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION)
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                .contact(new Contact()
                        .name("PandemicNet Support")
                        .url("https://github.com/your-org/pandemicnet")
                        .email("support@pandemicnet.app"));
    }

    /**
     * Configures server URLs for different environments.
     *
     * @return List of server configurations
     */
    private List<Server> getServers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");

        Server prodServer = new Server()
                .url("https://api.pandemicnet.app")
                .description("Production Server");

        return List.of(devServer, prodServer);
    }

    // Uncomment and customize if you need to configure model converters
    // @PostConstruct
    // public void configureModelConverters() {
    //     // Example: Configure model converters if needed
    //     SpringDocUtils.getConfig()
    //             .replaceWithClass(java.time.LocalDateTime.class, String.class);
    // }
}