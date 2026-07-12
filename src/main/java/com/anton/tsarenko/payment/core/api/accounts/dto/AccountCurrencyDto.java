package com.anton.tsarenko.payment.core.api.accounts.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Currency of an account.
 */
@Schema(description = "Account currency.")
public enum AccountCurrencyDto {
    /**
     * Euro.
     */
    EUR,
    /**
     * American dollar.
     */
    USD,
    /**
     * British pound.
     */
    GBP,
    /**
     * Swiss franc.
     */
    CHF,
    /**
     * Ukrainian hryvnia.
     */
    UAH;

    private final String value = super.toString().toLowerCase();

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
