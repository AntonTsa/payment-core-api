package com.anton.tsarenko.payment.core.api.users.repository;

import com.anton.tsarenko.payment.core.api.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
