package com.anton.tsarenko.payment.core.api.accounts.repository;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository interface for managing Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Transactional
    @Query("SELECT a from Account where a.userId = ?1")
    List<Account> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("delete from Account a where a.userId = ?1")
    void deleteByUserId(Long userId);


}
