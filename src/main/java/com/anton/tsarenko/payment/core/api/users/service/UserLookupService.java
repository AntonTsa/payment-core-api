package com.anton.tsarenko.payment.core.api.users.service;

/**
 * Public contract with accounts module. Provides method for verifying if user with provided id exists.
 */
public interface UserLookupService {
    /**
     * Method for verifying if user with provided id exists.
     *
     * @param id - provided user id.
     * @return true if user exists and false if user doesn't exist
     */
    boolean existedById(Long id);
}
