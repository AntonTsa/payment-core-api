package com.anton.tsarenko.payment.core.api.accounts.service;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import java.util.List;

/**
 * Public contract with users module. Provides methods for managing accounts belonging to a particular user.
 */
public interface AccountsLookupService {
    /**
     * Gets all existing accounts by user id.
     *
     * @param userId - provided user id
     * @return list of accounts with provided user id
     */
    List<Account> getAccountsByUserId(long userId);

    /**
     * Deletes all existing accounts by user id.
     *
     * @param userId - provided user id
     */
    void deleteAccountsByUserId(long userId);
}
