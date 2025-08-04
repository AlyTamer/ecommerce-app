package com.aly.ecomapp.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                title = "Eccommerce Application API",
                version = "1.0",
                description = "API documentation for Bright Skies Task to test out the Ecommerce Application"
        ),

        servers = @Server(
                        url = "http://localhost:8080",
                        description = "Local server")

)
@SecurityScheme(
        name="bearerAuth",
        description = "Basic JWT Authentication based on Username and Password + Admin/User Role",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER

)
public class SwaggerConfig {
}
