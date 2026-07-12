package com.anton.tsarenko.payment.core.api.accounts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountCurrency;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldSaveAndFindAccountById() {
        Account account = Account.builder()
                .userId(1L)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.parse("2026-06-27T10:00:00Z"))
                .build();

        Account savedAccount = accountRepository.saveAndFlush(account);

        assertThat(savedAccount.getId()).isNotNull();
        assertThat(accountRepository.findById(savedAccount.getId()))
                .isPresent()
                .get()
                .satisfies(foundAccount -> {
                    assertThat(foundAccount.getUserId()).isEqualTo(1L);
                    assertThat(foundAccount.getCurrency().name()).isEqualTo("EUR");
                    assertThat(foundAccount.getStatus()).isEqualTo(AccountStatus.ACTIVE);
                });
    }

    @Test
    void shouldSaveBlockedAccount() {
        Account account = Account.builder()
                .userId(2L)
                .currency(AccountCurrency.USD)
                .status(AccountStatus.BLOCKED)
                .createdAt(Instant.parse("2026-06-27T11:00:00Z"))
                .build();

        Account savedAccount = accountRepository.saveAndFlush(account);

        assertThat(savedAccount.getStatus()).isEqualTo(AccountStatus.BLOCKED);
        assertThat(savedAccount.getId()).isNotNull();
    }
}
