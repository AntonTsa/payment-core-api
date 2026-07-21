package com.anton.tsarenko.payment.core.api.accounts.service.impl;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.repository.AccountRepository;
import com.anton.tsarenko.payment.core.api.accounts.service.AccountsLookupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of public contract between modules accounts and users.
 */
@Service
@RequiredArgsConstructor
public class AccountsLookupServiceImpl implements AccountsLookupService {
    private final AccountRepository repository;

    @Override
    public List<Account> getAccountsByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void deleteAccountsByUserId(long userId) {
        repository.deleteByUserId(userId);
    }
}
