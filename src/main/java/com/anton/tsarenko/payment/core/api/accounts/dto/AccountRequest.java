package com.anton.tsarenko.payment.core.api.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing values from account requests.
 *
 * @param userId - account owner's user id
 * @param currency - account currency
 * @param status - account status
 */
@Builder
@Jacksonized
@Schema(description = "Request body for creating or replacing an account.")
public record AccountRequest(
        @Positive
        @Schema(description = "Owner user identifier.", example = "1")
        Long userId,
        @Schema(description = "ISO 4217 account currency. Defaults to EUR.", example = "EUR")
        AccountCurrencyDto currency,
        @Schema(description = "Account status. Defaults to ACTIVE.", example = "ACTIVE")
        AccountStatusDto status
) implements Serializable {}
