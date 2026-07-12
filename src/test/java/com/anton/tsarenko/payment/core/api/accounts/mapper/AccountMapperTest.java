package com.anton.tsarenko.payment.core.api.accounts.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.payment.core.api.accounts.dto.AccountCurrencyDto;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountRequest;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountResponse;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountStatusDto;
import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountCurrency;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AccountMapper}.
 */
class AccountMapperTest {

    private static final Long ACCOUNT_ID = 23L;
    private static final Instant CREATED_AT = Instant.parse("2026-06-30T10:00:00Z");
    private static final Long USER_ID = 42L;

    private final AccountMapper accountMapper = new AccountMapper();

    @Test
    @DisplayName("""
            GIVEN a valid account request
            WHEN converting request to account entity
            THEN account entity is created with request fields and no id
            """)
    void shouldConvertAccountRequestToAccountEntity() {
        AccountRequest request = AccountRequest.builder()
                .userId(USER_ID)
                .currency(AccountCurrencyDto.USD)
                .status(AccountStatusDto.BLOCKED)
                .build();

        Account account = accountMapper.toAccount(request);

        assertThat(account)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("userId", USER_ID)
                .hasFieldOrPropertyWithValue("currency", AccountCurrency.USD)
                .hasFieldOrPropertyWithValue("status", AccountStatus.BLOCKED)
                .hasFieldOrPropertyWithValue("createdAt", null);
    }

    @Test
    @DisplayName("""
            GIVEN account request without currency and status
            WHEN converting request to account entity
            THEN default EUR currency and ACTIVE status are used
            """)
    void shouldApplyDefaultCurrencyAndStatus() {
        AccountRequest request = AccountRequest.builder()
                .userId(USER_ID)
                .build();

        Account account = accountMapper.toAccount(request);

        assertThat(account.getCurrency().name()).isEqualTo("EUR");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("""
            GIVEN an account entity with all fields
            WHEN converting account to response DTO
            THEN response contains all account fields correctly mapped
            """)
    void shouldConvertAccountEntityToAccountResponse() {
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .userId(USER_ID)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(CREATED_AT)
                .build();

        AccountResponse response = accountMapper.toAccountResponse(account);

        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", ACCOUNT_ID)
                .hasFieldOrPropertyWithValue("userId", USER_ID)
                .hasFieldOrPropertyWithValue("currency", AccountCurrencyDto.EUR)
                .hasFieldOrPropertyWithValue("status", AccountStatusDto.ACTIVE)
                .hasFieldOrPropertyWithValue("createdAt", CREATED_AT);
    }
}
