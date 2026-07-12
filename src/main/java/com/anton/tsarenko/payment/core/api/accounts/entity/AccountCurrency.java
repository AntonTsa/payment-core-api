package com.anton.tsarenko.payment.core.api.accounts.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Currency of an account.
 */
@Schema(description = "Account currency.")
public enum AccountCurrency {
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
    UAH
}
