package com.anton.tsarenko.payment.core.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI documentation configuration.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Core API",
                version = "1.0.0",
                description = "REST API for user and account management in Payment Core.",
                contact = @Contact(name = "Payment Core API Team"),
                license = @License(name = "MIT")
        ),
        tags = {
                @Tag(name = "Users", description = "CRUD operations for users"),
                @Tag(name = "Accounts", description = "CRUD operations for accounts")
        }
)
public class OpenApiConfig {
}
