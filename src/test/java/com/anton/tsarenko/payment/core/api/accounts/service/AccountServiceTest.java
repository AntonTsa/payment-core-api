package com.anton.tsarenko.payment.core.api.accounts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountCurrency;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountStatus;
import com.anton.tsarenko.payment.core.api.accounts.repository.AccountRepository;
import com.anton.tsarenko.payment.core.api.accounts.service.impl.AccountServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link AccountServiceImpl}.
 */
class AccountServiceTest {

    private static final Long ACCOUNT_ID = 23L;
    private static final Instant CREATED_AT = Instant.parse("2026-06-27T10:00:00Z");

    private AccountRepository accountRepository;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    @DisplayName("""
            GIVEN a new account with existing user id and repository returns saved account with generated id
            WHEN creating account
            THEN saved account id is returned
            """)
    void shouldReturnSavedAccountIdWhenCreatingAccount() {
        Account account = Account.builder()
                .userId(1L)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(CREATED_AT)
                .build();
        Account savedAccount = Account.builder()
                .id(ACCOUNT_ID)
                .userId(1L)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(CREATED_AT)
                .build();
        when(accountRepository.save(account)).thenReturn(savedAccount);

        Long accountId = accountService.createAccount(account);

        assertThat(accountId).isEqualTo(ACCOUNT_ID);
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("""
            GIVEN existing account id with existing user id
            WHEN finding account by id
            THEN account is returned
            """)
    void shouldReturnAccountWhenFindingByExistingId() {
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .userId(1L)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(CREATED_AT)
                .build();

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Account foundAccount = accountService.findById(ACCOUNT_ID);

        assertThat(foundAccount).isSameAs(account);
        verify(accountRepository).findById(ACCOUNT_ID);
    }

    @Test
    @DisplayName("""
            GIVEN missing account id
            WHEN finding account by id
            THEN runtime exception is thrown
            """)
    void shouldThrowExceptionWhenFindingMissingAccount() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(ACCOUNT_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
        verify(accountRepository).findById(ACCOUNT_ID);
    }

    @Test
    @DisplayName("""
            GIVEN missing account id and replacement account
            WHEN replacing account
            THEN runtime exception is thrown and account is not saved
            """)
    void shouldThrowExceptionWhenReplacingMissingAccount() {
        Account replacementAccount = Account.builder()
                .userId(2L)
                .currency(AccountCurrency.USD)
                .status(AccountStatus.BLOCKED)
                .build();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.replaceAccount(ACCOUNT_ID, replacementAccount))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository, never()).save(Mockito.any(Account.class));
    }

    @Test
    @DisplayName("""
            GIVEN existing account id
            WHEN deleting account
            THEN repository deletes account by id
            """)
    void shouldDeleteAccountById() {
        accountService.deleteAccount(ACCOUNT_ID);

        verify(accountRepository).deleteById(ACCOUNT_ID);
    }
}
