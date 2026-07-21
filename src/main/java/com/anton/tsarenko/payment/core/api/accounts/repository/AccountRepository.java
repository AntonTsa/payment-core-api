package com.anton.tsarenko.payment.core.api.accounts.repository;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Method for getting all accounts connected to a specific user.
     *
     * @param userId - id of a specific user
     * @return @link{List<Account>} - list of accounts connected to a specific user
     */
    @Transactional
    @Query("SELECT a FROM Account a WHERE a.userId = ?1")
    List<Account> findByUserId(Long userId);

    /**
     * Method for deleting all accounts connected to a specific user.
     *
     * @param userId - id of a specific user
     */
    @Transactional
    @Modifying
    @Query("delete FROM Account a WHERE a.userId = ?1")
    void deleteByUserId(Long userId);

}
