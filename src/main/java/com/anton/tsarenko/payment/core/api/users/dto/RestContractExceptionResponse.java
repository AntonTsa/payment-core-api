package com.anton.tsarenko.payment.core.api.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;

/**
 * DTO for response with exception.
 *
 * @param timestamp - timestamp of exception
 * @param error - type of exception
 * @param message - message of exception
 */
@Builder
@Schema(description = "Standard REST error response.")
public record RestContractExceptionResponse(
        @Schema(description = "Timestamp of exception.", example = "2026-06-30T10:00:00")
        LocalDateTime timestamp,
        @Schema(description = "HTTP error reason.", example = "Bad Request")
        String error,
        @Schema(description = "Error details.", example = "email: must be a well-formed email address")
        String message
) implements Serializable {}
