package com.anton.tsarenko.payment.core.api.accounts.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Status of an account.
 */
@Schema(description = "Account status.")
public enum AccountStatus {
    /**
     * Account can be used for operations.
     */
    ACTIVE,
    /**
     * Account is blocked from operations.
     */
    BLOCKED
}
