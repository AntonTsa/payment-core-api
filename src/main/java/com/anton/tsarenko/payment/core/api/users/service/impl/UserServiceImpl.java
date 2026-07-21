package com.anton.tsarenko.payment.core.api.users.service.impl;

import com.anton.tsarenko.payment.core.api.accounts.service.AccountsLookupService;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import com.anton.tsarenko.payment.core.api.users.repository.UserRepository;
import com.anton.tsarenko.payment.core.api.users.service.UserLookupService;
import com.anton.tsarenko.payment.core.api.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserLookupService {

    private final UserRepository userRepository;
    private final AccountsLookupService accountsLookupService;

    @Override
    public Long createUser(User user) {
        return userRepository.save(user).getId();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void replaceUser(Long id, User user) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user1.setEmail(user.getEmail());
        userRepository.save(user1);
    }

    @Override
    public void deleteUser(Long id) {
        accountsLookupService.deleteAccountsByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public boolean existedById(Long id) {
        return userRepository.existsById(id);
    }
}
