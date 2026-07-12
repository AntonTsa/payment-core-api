package com.anton.tsarenko.payment.core.api.accounts.service;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;

/**
 * CRUD Service for account management.
 */
public interface AccountService {
    /**
     * Creating a new account.
     *
     * @param account - account, to be created.
     * @return id of the created account.
     */
    Long createAccount(Account account);

    /**
     * Getting account by given id.
     *
     * @param id - given id.
     * @return account with given id.
     */
    Account findById(Long id);

    /**
     * Updating account.
     *
     * @param id - given valid account id.
     * @param account - updated account info.
     */
    void replaceAccount(Long id, Account account);

    /**
     * Deleting account by given id.
     *
     * @param id - given id.
     */
    void deleteAccount(Long id);
}
