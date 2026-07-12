package com.anton.tsarenko.payment.core.api.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.io.Serializable;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing values from requests.
 *
 * @param email - user email in request
 */
@Builder
@Jacksonized
@Schema(description = "Request body for creating or replacing a user.")
public record UserRequest(
        @Email
        @Schema(description = "User email address.", example = "user@example.com")
        String email
) implements Serializable {}
