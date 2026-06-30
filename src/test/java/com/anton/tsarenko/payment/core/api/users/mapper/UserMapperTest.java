package com.anton.tsarenko.payment.core.api.users.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.payment.core.api.users.dto.UserRequest;
import com.anton.tsarenko.payment.core.api.users.dto.UserResponse;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserMapper}.
 */
class UserMapperTest {

    private static final Instant CREATED_AT = Instant.parse("2026-06-30T10:00:00Z");
    private static final String EMAIL = "test@example.com";

    private final UserMapper userMapper = new UserMapper();

    @Test
    @DisplayName("""
            GIVEN a valid user request with email
            WHEN converting request to user entity
            THEN user entity is created with correct email and no id
            """)
    void shouldConvertUserRequestToUserEntity() {
        // Given
        UserRequest request = UserRequest.builder()
                .email(EMAIL)
                .build();

        // When
        User user = userMapper.toUser(request);

        // Then
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", EMAIL)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("createdAt", null);
    }

    @Test
    @DisplayName("""
            GIVEN a user request with email containing special characters
            WHEN converting request to user entity
            THEN user entity preserves email exactly
            """)
    void shouldPreserveEmailWithSpecialCharacters() {
        // Given
        String specialEmail = "user+tag@sub.example.co.uk";
        UserRequest request = UserRequest.builder()
                .email(specialEmail)
                .build();

        // When
        User user = userMapper.toUser(request);

        // Then
        assertThat(user.getEmail()).isEqualTo(specialEmail);
    }

    @Test
    @DisplayName("""
            GIVEN a user entity with id, email and created timestamp
            WHEN converting user to response DTO
            THEN response contains all user fields correctly mapped
            """)
    void shouldConvertUserEntityToUserResponse() {
        // Given
        Long userId = 123L;
        User user = User.builder()
                .id(userId)
                .email(EMAIL)
                .createdAt(CREATED_AT)
                .build();

        // When
        UserResponse response = userMapper.toUserResponse(user);

        // Then
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("email", EMAIL)
                .hasFieldOrPropertyWithValue("createdAt", CREATED_AT);
    }

    @Test
    @DisplayName("""
            GIVEN a user entity with null created timestamp
            WHEN converting user to response DTO
            THEN response is created with null created timestamp
            """)
    void shouldHandleNullCreatedAtWhenConvertingToResponse() {
        // Given
        User user = User.builder()
                .id(1L)
                .email(EMAIL)
                .createdAt(null)
                .build();

        // When
        UserResponse response = userMapper.toUserResponse(user);

        // Then
        assertThat(response.createdAt()).isNull();
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("""
            GIVEN a user entity with all fields populated
            WHEN converting to response and back to entity
            THEN response contains all original data
            """)
    void shouldPreserveAllDataWhenConvertingToResponse() {
        // Given
        User originalUser = User.builder()
                .id(999L)
                .email("data.preservation@test.com")
                .createdAt(CREATED_AT)
                .build();

        // When
        UserResponse response = userMapper.toUserResponse(originalUser);

        // Then
        assertThat(response.id()).isEqualTo(originalUser.getId());
        assertThat(response.email()).isEqualTo(originalUser.getEmail());
        assertThat(response.createdAt()).isEqualTo(originalUser.getCreatedAt());
    }

    @Test
    @DisplayName("""
            GIVEN two identical user requests
            WHEN converting both to user entities
            THEN both entities have the same email field
            """)
    void shouldCreateIdenticalUsersFromIdenticalRequests() {
        // Given
        UserRequest request1 = UserRequest.builder().email(EMAIL).build();
        UserRequest request2 = UserRequest.builder().email(EMAIL).build();

        // When
        User user1 = userMapper.toUser(request1);
        User user2 = userMapper.toUser(request2);

        // Then
        assertThat(user1.getEmail()).isEqualTo(user2.getEmail());
        assertThat(user1.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("""
            GIVEN a user entity with different id values
            WHEN converting multiple users to responses
            THEN each response maintains its unique id
            """)
    void shouldPreserveUniqueIdsWhenConvertingMultipleUsers() {
        // Given
        User user1 = User.builder().id(1L).email("user1@test.com").createdAt(CREATED_AT).build();
        User user2 = User.builder().id(2L).email("user2@test.com").createdAt(CREATED_AT).build();
        User user3 = User.builder().id(3L).email("user3@test.com").createdAt(CREATED_AT).build();

        // When
        UserResponse response1 = userMapper.toUserResponse(user1);
        UserResponse response2 = userMapper.toUserResponse(user2);
        UserResponse response3 = userMapper.toUserResponse(user3);

        // Then
        assertThat(response1.id()).isEqualTo(1L);
        assertThat(response2.id()).isEqualTo(2L);
        assertThat(response3.id()).isEqualTo(3L);
    }
}
