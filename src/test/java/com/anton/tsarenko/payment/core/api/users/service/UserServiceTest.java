package com.anton.tsarenko.payment.core.api.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anton.tsarenko.payment.core.api.users.entity.User;
import com.anton.tsarenko.payment.core.api.users.repository.UserRepository;
import com.anton.tsarenko.payment.core.api.users.service.impl.UserServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
class UserServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-06-27T10:00:00Z");

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("""
            GIVEN a new user and repository returns saved user with generated id
            WHEN creating user
            THEN saved user id is returned
            """)
    void shouldReturnSavedUserIdWhenCreatingUser() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .createdAt(CREATED_AT)
                .build();
        User savedUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .createdAt(CREATED_AT)
                .build();
        when(userRepository.save(user)).thenReturn(savedUser);

        // When
        Long userId = userService.createUser(user);

        // Then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("""
            GIVEN existing user id
            WHEN finding user by id
            THEN user is returned
            """)
    void shouldReturnUserWhenFindingByExistingId() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .createdAt(CREATED_AT)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User foundUser = userService.findById(userId);

        // Then
        assertThat(foundUser).isSameAs(user);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("""
            GIVEN missing user id
            WHEN finding user by id
            THEN runtime exception is thrown
            """)
    void shouldThrowExceptionWhenFindingMissingUser() {
        // Given
        Long userId = 404L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("""
            GIVEN existing user and replacement user
            WHEN replacing user
            THEN existing user email is updated and saved
            """)
    void shouldUpdateExistingUserEmailWhenReplacingUser() {
        // Given
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .email("old@example.com")
                .createdAt(CREATED_AT)
                .build();
        User replacementUser = User.builder()
                .email("new@example.com")
                .createdAt(Instant.parse("2026-06-27T11:00:00Z"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        userService.replaceUser(userId, replacementUser);

        // Then
        assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("""
            GIVEN missing user id and replacement user
            WHEN replacing user
            THEN runtime exception is thrown and user is not saved
            """)
    void shouldThrowExceptionWhenReplacingMissingUser() {
        // Given
        Long userId = 404L;
        User replacementUser = User.builder()
                .email("new@example.com")
                .createdAt(CREATED_AT)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.replaceUser(userId, replacementUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("""
            GIVEN existing user id
            WHEN deleting user
            THEN repository deletes user by id
            """)
    void shouldDeleteUserById() {
        // Given
        Long userId = 1L;

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }
}
