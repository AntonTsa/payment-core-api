package com.anton.tsarenko.payment.core.api.accounts.repository;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
