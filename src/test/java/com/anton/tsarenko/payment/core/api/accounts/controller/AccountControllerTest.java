package com.anton.tsarenko.payment.core.api.accounts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anton.tsarenko.payment.core.api.accounts.dto.AccountCurrencyDto;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountRequest;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountResponse;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountStatusDto;
import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountCurrency;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountStatus;
import com.anton.tsarenko.payment.core.api.accounts.mapper.AccountMapper;
import com.anton.tsarenko.payment.core.api.accounts.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link AccountController}.
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private static final Long ACCOUNT_ID = 23L;
    private static final Long USER_ID = 42L;
    private static final Instant CREATED_AT = Instant.parse("2026-06-30T10:00:00Z");
    private static final String REQUEST_URI = "http://localhost:8080/api/v1/accounts";

    @Mock
    private AccountService accountService;

    @Mock
    private AccountMapper mapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AccountController accountController;

    @Test
    @DisplayName("""
            GIVEN a valid account request
            WHEN creating a new account
            THEN response status is 201 Created with location URI containing account id
            """)
    void shouldCreateAccountAndReturnLocationUri() {
        AccountRequest accountRequest = AccountRequest.builder()
                .userId(USER_ID)
                .currency(AccountCurrencyDto.EUR)
                .status(AccountStatusDto.ACTIVE)
                .build();
        Account account = Account.builder()
                .userId(USER_ID)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .build();
        when(mapper.toAccount(accountRequest)).thenReturn(account);
        when(accountService.createAccount(account)).thenReturn(ACCOUNT_ID);
        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

        ResponseEntity<URI> response = accountController.create(accountRequest, httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(REQUEST_URI + "/" + ACCOUNT_ID));
        assertThat(response.getBody()).isNull();
        verify(mapper).toAccount(accountRequest);
        verify(accountService).createAccount(account);
    }

    @Test
    @DisplayName("""
            GIVEN a valid account id
            WHEN fetching account by id
            THEN response status is 200 OK with account response body
            """)
    void shouldGetAccountByIdAndReturnAccountResponse() {
        AccountResponse accountResponse = AccountResponse.builder()
                .id(ACCOUNT_ID)
                .userId(USER_ID)
                .currency(AccountCurrencyDto.EUR)
                .status(AccountStatusDto.ACTIVE)
                .createdAt(CREATED_AT)
                .build();
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .userId(USER_ID)
                .currency(AccountCurrency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(CREATED_AT)
                .build();
        when(accountService.findById(ACCOUNT_ID)).thenReturn(account);
        when(mapper.toAccountResponse(account)).thenReturn(accountResponse);

        ResponseEntity<AccountResponse> response = accountController.get(ACCOUNT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(accountResponse);
        verify(accountService).findById(ACCOUNT_ID);
        verify(mapper).toAccountResponse(account);
    }

    @Test
    @DisplayName("""
            GIVEN an existing account id and valid update request
            WHEN updating account
            THEN response status is 204 No Content and service is called with correct data
            """)
    void shouldUpdateAccountAndReturnNoContent() {
        AccountRequest accountRequest = AccountRequest.builder()
                .userId(USER_ID)
                .currency(AccountCurrencyDto.USD)
                .status(AccountStatusDto.BLOCKED)
                .build();
        Account account = Account.builder()
                .userId(USER_ID)
                .currency(AccountCurrency.USD)
                .status(AccountStatus.BLOCKED)
                .build();
        when(mapper.toAccount(accountRequest)).thenReturn(account);
        doNothing().when(accountService).replaceAccount(ACCOUNT_ID, account);

        ResponseEntity<Void> response = accountController.update(ACCOUNT_ID, accountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(accountService).replaceAccount(ACCOUNT_ID, account);
        verify(mapper).toAccount(accountRequest);
    }

    @Test
    @DisplayName("""
            GIVEN an existing account id
            WHEN deleting account
            THEN response status is 204 No Content and service delete is called
            """)
    void shouldDeleteAccountAndReturnNoContent() {
        doNothing().when(accountService).deleteAccount(ACCOUNT_ID);

        ResponseEntity<Void> response = accountController.delete(ACCOUNT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(accountService).deleteAccount(ACCOUNT_ID);
    }

    @Test
    @DisplayName("""
            GIVEN account service throws EmptyResultDataAccessException during delete
            WHEN deleting non-existent account
            THEN exception is propagated and no response is returned
            """)
    void shouldPropagateEmptyResultDataAccessExceptionWhenDeletingNonExistentAccount() {
        EmptyResultDataAccessException exception = new EmptyResultDataAccessException(1);
        doThrow(exception).when(accountService).deleteAccount(ACCOUNT_ID);

        assertThatThrownBy(() -> accountController.delete(ACCOUNT_ID))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .isEqualTo(exception);
        verify(accountService).deleteAccount(ACCOUNT_ID);
    }

    @Test
    @DisplayName("""
            GIVEN account service throws RuntimeException
            WHEN accessing account endpoint
            THEN exception is propagated to GlobalExceptionHandler
            """)
    void shouldPropagateGenericRuntimeExceptionFromService() {
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(accountService.findById(ACCOUNT_ID)).thenThrow(exception);

        assertThatThrownBy(() -> accountController.get(ACCOUNT_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
        verify(accountService).findById(ACCOUNT_ID);
    }
}
