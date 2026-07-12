package com.anton.tsarenko.payment.core.api.accounts.service.impl;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.repository.AccountRepository;
import com.anton.tsarenko.payment.core.api.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of account CRUD service.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Long createAccount(Account account) {
        return accountRepository.save(account).getId();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public void replaceAccount(Long id, Account account) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        existingAccount.setUserId(account.getUserId());
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setStatus(account.getStatus());
        accountRepository.save(existingAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
