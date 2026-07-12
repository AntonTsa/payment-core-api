package com.anton.tsarenko.payment.core.api.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing body of account response.
 *
 * @param id - account id
 * @param userId - account owner's user id
 * @param currency - account currency
 * @param status - account status
 * @param createdAt - timestamp of account creation
 */
@Builder
@Jacksonized
@Schema(description = "Account response.")
public record AccountResponse(
        @Schema(
                description = "Account identifier.",
                example = "6c17d1b7-6b90-4be3-9f7c-a22b5026b971"
        )
        Long id,
        @Schema(description = "Owner user identifier.", example = "1")
        Long userId,
        @Schema(description = "ISO 4217 account currency.", example = "EUR")
        AccountCurrencyDto currency,
        @Schema(description = "Account status.", example = "ACTIVE")
        AccountStatusDto status,
        @Schema(description = "Timestamp of account creation.", example = "2026-06-30T10:00:00Z")
        Instant createdAt
) implements Serializable {}
