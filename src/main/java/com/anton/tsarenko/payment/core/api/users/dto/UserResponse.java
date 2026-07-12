package com.anton.tsarenko.payment.core.api.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing body of response.
 *
 * @param id - user's id
 * @param email - user's email
 * @param createdAt - timestamp of user creation
 */
@Builder
@Jacksonized
@Schema(description = "User response.")
public record UserResponse(
        @Schema(description = "User identifier.", example = "1")
        Long id,
        @Schema(description = "User email address.", example = "user@example.com")
        String email,
        @Schema(description = "Timestamp of user creation.", example = "2026-06-30T10:00:00Z")
        Instant createdAt
) implements Serializable {}
