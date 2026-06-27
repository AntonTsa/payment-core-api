package com.anton.tsarenko.payment.core.api.users.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.anton.tsarenko.payment.core.api.users.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = User.builder()
                .email("user@example.com")
                .createdAt(Instant.parse("2026-06-27T10:00:00Z"))
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(userRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(User::getEmail)
                .isEqualTo("user@example.com");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User firstUser = User.builder()
                .email("duplicate@example.com")
                .createdAt(Instant.parse("2026-06-27T10:00:00Z"))
                .build();

        User secondUser = User.builder()
                .email("duplicate@example.com")
                .createdAt(Instant.parse("2026-06-27T11:00:00Z"))
                .build();

        userRepository.saveAndFlush(firstUser);

        assertThatThrownBy(() -> userRepository.saveAndFlush(secondUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
