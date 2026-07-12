package com.anton.tsarenko.payment.core.api.accounts.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Status of an account.
 */
@Schema(description = "Account status.")
public enum AccountStatusDto {
    /**
     * Account can be used for operations.
     */
    ACTIVE,
    /**
     * Account is blocked from operations.
     */
    BLOCKED;

    private final String value = super.toString().toLowerCase();

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
