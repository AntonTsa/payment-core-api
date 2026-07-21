package com.anton.tsarenko.payment.core.api.accounts.service.impl;

import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.repository.AccountRepository;
import com.anton.tsarenko.payment.core.api.accounts.service.AccountService;
import com.anton.tsarenko.payment.core.api.users.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of account CRUD service.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserLookupService userLookupService;

    @Override
    public Long createAccount(Account account) {
        if (!userLookupService.existedById(account.getUserId())) {
            throw new RuntimeException("User with provided id does not exist.");
        }
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
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setStatus(account.getStatus());
        accountRepository.save(existingAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
