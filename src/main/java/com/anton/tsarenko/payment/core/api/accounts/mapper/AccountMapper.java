package com.anton.tsarenko.payment.core.api.accounts.mapper;

import com.anton.tsarenko.payment.core.api.accounts.dto.AccountCurrencyDto;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountRequest;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountResponse;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountStatusDto;
import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountCurrency;
import com.anton.tsarenko.payment.core.api.accounts.entity.AccountStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Account entities and DTOs.
 */
@Component
public class AccountMapper {

    /**
     * Convert an AccountRequest DTO to an Account entity.
     *
     * @param request - the AccountRequest DTO containing account data
     * @return - Account entity
     */
    public Account toAccount(AccountRequest request) {
        return Account.builder()
                .userId(request.userId())
                .currency(toAccountCurrency(request.currency()))
                .status(toAccountStatus(request.status()))
                .build();
    }

    private AccountStatus toAccountStatus(AccountStatusDto status) {
        return (status != null)
                ? AccountStatus.valueOf(status.name())
                : AccountStatus.ACTIVE;
    }

    private AccountCurrency toAccountCurrency(AccountCurrencyDto currency) {
        return (currency != null)
                ? AccountCurrency.valueOf(currency.name())
                : AccountCurrency.EUR;
    }

    /**
     * Convert an Account entity to an AccountResponse DTO.
     *
     * @param account - the Account entity
     * @return - AccountResponse DTO
     */
    public AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .currency(toAccountCurrencyDto(account.getCurrency()))
                .status(toAccountStatusDto(account.getStatus()))
                .createdAt(account.getCreatedAt())
                .build();
    }

    private AccountStatusDto toAccountStatusDto(AccountStatus status) {
        return (status != null)
                ? AccountStatusDto.valueOf(status.name())
                : AccountStatusDto.ACTIVE;
    }

    private AccountCurrencyDto toAccountCurrencyDto(AccountCurrency currency) {
        return (currency != null)
                ? AccountCurrencyDto.valueOf(currency.name())
                : AccountCurrencyDto.EUR;
    }
}
