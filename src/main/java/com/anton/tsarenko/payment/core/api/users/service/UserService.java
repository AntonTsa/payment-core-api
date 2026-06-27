package com.anton.tsarenko.payment.core.api.users.service;

import com.anton.tsarenko.payment.core.api.users.entity.User;

/**
 * CRUD Service for user management.
 */
public interface UserService {
    /**
     * Creating a new user.
     *
     * @param user - user, to be created.
     * @return id of the created user.
     */
    Long createUser(User user);

    /**
     * Getting user by given id.
     *
     * @param id - given id.
     * @return user with given id.
     */
    User findById(Long id);

    /**
     * Updating user.
     *
     * @param id - given valid user id.
     * @param user - updated user info.
     */
    void replaceUser(Long id, User user);

    /**
     * Deleting user by given id.
     *
     * @param id - given id.
     */
    void deleteUser(Long id);

}
