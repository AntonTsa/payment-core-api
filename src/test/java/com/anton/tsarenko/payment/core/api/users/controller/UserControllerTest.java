package com.anton.tsarenko.payment.core.api.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anton.tsarenko.payment.core.api.users.dto.UserRequest;
import com.anton.tsarenko.payment.core.api.users.dto.UserResponse;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import com.anton.tsarenko.payment.core.api.users.mapper.UserMapper;
import com.anton.tsarenko.payment.core.api.users.service.UserService;
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
 * Unit tests for {@link UserController}.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final long USER_ID = 42L;
    private static final String USER_EMAIL = "test@example.com";
    private static final Instant CREATED_AT = Instant.parse("2026-06-30T10:00:00Z");
    private static final String REQUEST_URI = "http://localhost:8080/api/users";

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("""
            GIVEN a valid user request with email
            WHEN creating a new user
            THEN response status is 201 Created with location URI containing user id
            """)
    void shouldCreateUserAndReturnLocationUri() {
        // Given
        UserRequest userRequest = UserRequest.builder().email(USER_EMAIL).build();
        User user = User.builder().email(USER_EMAIL).build();
        when(mapper.toUser(userRequest)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(USER_ID);
        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

        // When
        ResponseEntity<URI> response = userController.create(userRequest, httpServletRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(REQUEST_URI + "/" + USER_ID));
        assertThat(response.getBody()).isNull();
        verify(mapper).toUser(userRequest);
        verify(userService).createUser(user);
    }

    @Test
    @DisplayName("""
            GIVEN a user request with email
            WHEN creating a user
            THEN mapper converts request to user entity before saving
            """)
    void shouldMapUserRequestToEntityBeforeSaving() {
        // Given
        UserRequest userRequest = UserRequest.builder().email(USER_EMAIL).build();
        User expectedUser = User.builder().email(USER_EMAIL).build();
        when(mapper.toUser(userRequest)).thenReturn(expectedUser);
        when(userService.createUser(expectedUser)).thenReturn(USER_ID);
        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

        // When
        userController.create(userRequest, httpServletRequest);

        // Then
        verify(mapper).toUser(userRequest);
        verify(userService).createUser(expectedUser);
    }

    @Test
    @DisplayName("""
            GIVEN a valid user id
            WHEN fetching user by id
            THEN response status is 200 OK with user response body
            """)
    void shouldGetUserByIdAndReturnUserResponse() {
        // Given
        UserResponse userResponse = UserResponse.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .createdAt(CREATED_AT)
                .build();
        User user = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .createdAt(CREATED_AT)
                .build();
        when(userService.findById(USER_ID)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.getNote(USER_ID);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userResponse);
        verify(userService).findById(USER_ID);
        verify(mapper).toUserResponse(user);
    }

    @Test
    @DisplayName("""
            GIVEN an existing user id and valid update request
            WHEN updating user
            THEN response status is 204 No Content and service is called with correct data
            """)
    void shouldUpdateUserAndReturnNoContent() {
        // Given
        UserRequest userRequest = UserRequest.builder().email("newemail@example.com").build();
        User user = User.builder().email("newemail@example.com").build();
        when(mapper.toUser(userRequest)).thenReturn(user);
        doNothing().when(userService).replaceUser(USER_ID, user);

        // When
        ResponseEntity<Void> response = userController.updateNote(USER_ID, userRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(userService).replaceUser(USER_ID, user);
        verify(mapper).toUser(userRequest);
    }

    @Test
    @DisplayName("""
            GIVEN an existing user id
            WHEN deleting user
            THEN response status is 204 No Content and service delete is called
            """)
    void shouldDeleteUserAndReturnNoContent() {
        // Given
        doNothing().when(userService).deleteUser(USER_ID);

        // When
        ResponseEntity<Void> response = userController.deleteNote(USER_ID);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(userService).deleteUser(USER_ID);
    }

    @Test
    @DisplayName("""
            GIVEN multiple user creation requests with different emails
            WHEN creating users sequentially
            THEN each user gets unique id in location URI
            """)
    void shouldHandleMultipleUserCreationsWithUniqueIds() {
        // Given
        UserRequest request1 = UserRequest.builder().email("user1@example.com").build();
        UserRequest request2 = UserRequest.builder().email("user2@example.com").build();
        User user1 = User.builder().email("user1@example.com").build();
        User user2 = User.builder().email("user2@example.com").build();

        when(mapper.toUser(request1)).thenReturn(user1);
        when(mapper.toUser(request2)).thenReturn(user2);
        when(userService.createUser(user1)).thenReturn(1L);
        when(userService.createUser(user2)).thenReturn(2L);
        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

        // When
        ResponseEntity<URI> response1 = userController.create(request1, httpServletRequest);
        ResponseEntity<URI> response2 = userController.create(request2, httpServletRequest);

        // Then
        assertThat(response1.getHeaders().getLocation()).isEqualTo(URI.create(REQUEST_URI + "/1"));
        assertThat(response2.getHeaders().getLocation()).isEqualTo(URI.create(REQUEST_URI + "/2"));
    }

    @Test
    @DisplayName("""
            GIVEN valid positive user id
            WHEN getting user
            THEN service is called with exact id provided
            """)
    void shouldPassCorrectIdToServiceWhenGettingUser() {
        // Given
        long providedId = 999L;
        User user = User.builder().id(providedId).email(USER_EMAIL).build();
        when(userService.findById(providedId)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(UserResponse.builder().id(providedId).build());

        // When
        userController.getNote(providedId);

        // Then
        verify(userService).findById(providedId);
    }

    @Test
    @DisplayName("""
            GIVEN user update request with new email
            WHEN updating user
            THEN mapper converts request to entity and service replaces user
            """)
    void shouldMapAndReplaceUserOnUpdate() {
        // Given
        String newEmail = "updated@example.com";
        UserRequest updateRequest = UserRequest.builder().email(newEmail).build();
        User updatedUser = User.builder().email(newEmail).build();
        when(mapper.toUser(updateRequest)).thenReturn(updatedUser);
        doNothing().when(userService).replaceUser(USER_ID, updatedUser);

        // When
        userController.updateNote(USER_ID, updateRequest);

        // Then
        verify(mapper).toUser(updateRequest);
        verify(userService).replaceUser(USER_ID, updatedUser);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws EmptyResultDataAccessException
            WHEN fetching non-existent user
            THEN exception is propagated to GlobalExceptionHandler
            """)
    void shouldPropagateEmptyResultDataAccessExceptionWhenUserNotFound() {
        // Given
        EmptyResultDataAccessException exception =
                new EmptyResultDataAccessException(1);
        when(userService.findById(USER_ID)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> userController.getNote(USER_ID))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .isEqualTo(exception);
        verify(userService).findById(USER_ID);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws EmptyResultDataAccessException during delete
            WHEN deleting non-existent user
            THEN exception is propagated and no response is returned
            """)
    void shouldPropagateEmptyResultDataAccessExceptionWhenDeletingNonExistentUser() {
        // Given
        EmptyResultDataAccessException exception =
                new EmptyResultDataAccessException(1);
        doThrow(exception).when(userService).deleteUser(USER_ID);

        // When & Then
        assertThatThrownBy(() -> userController.deleteNote(USER_ID))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .isEqualTo(exception);
        verify(userService).deleteUser(USER_ID);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws EmptyResultDataAccessException during update
            WHEN updating non-existent user
            THEN exception is propagated
            """)
    void shouldPropagateEmptyResultDataAccessExceptionWhenUpdatingNonExistentUser() {
        // Given
        UserRequest updateRequest = UserRequest.builder().email("new@example.com").build();
        User user = User.builder().email("new@example.com").build();
        EmptyResultDataAccessException exception =
                new EmptyResultDataAccessException(1);
        when(mapper.toUser(updateRequest)).thenReturn(user);
        doThrow(exception).when(userService).replaceUser(USER_ID, user);

        // When & Then
        assertThatThrownBy(() -> userController.updateNote(USER_ID, updateRequest))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .isEqualTo(exception);
        verify(userService).replaceUser(USER_ID, user);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws RuntimeException
            WHEN accessing user endpoint
            THEN exception is propagated to GlobalExceptionHandler
            """)
    void shouldPropagateGenericRuntimeExceptionFromService() {
        // Given
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(userService.findById(USER_ID)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> userController.getNote(USER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
        verify(userService).findById(USER_ID);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws RuntimeException during create
            WHEN creating user
            THEN exception is propagated
            """)
    void shouldPropagateGenericExceptionWhenCreatingUser() {
        // Given
        UserRequest userRequest = UserRequest.builder().email(USER_EMAIL).build();
        User user = User.builder().email(USER_EMAIL).build();
        RuntimeException exception = new RuntimeException("Service temporarily unavailable");
        when(mapper.toUser(userRequest)).thenReturn(user);
        doThrow(exception).when(userService).createUser(user);
        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

        // When & Then
        assertThatThrownBy(() -> userController.create(userRequest, httpServletRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service temporarily unavailable");
        verify(userService).createUser(user);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws IllegalArgumentException
            WHEN updating user with invalid data
            THEN exception is propagated
            """)
    void shouldPropagateIllegalArgumentExceptionOnInvalidUpdate() {
        // Given
        UserRequest updateRequest = UserRequest.builder().email("invalid@").build();
        User user = User.builder().email("invalid@").build();
        IllegalArgumentException exception = new IllegalArgumentException("Invalid email format");
        when(mapper.toUser(updateRequest)).thenReturn(user);
        doThrow(exception).when(userService).replaceUser(USER_ID, user);

        // When & Then
        assertThatThrownBy(() -> userController.updateNote(USER_ID, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
        verify(userService).replaceUser(USER_ID, user);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws NullPointerException
            WHEN service has internal null reference error
            THEN exception is propagated
            """)
    void shouldPropagateNullPointerExceptionFromService() {
        // Given
        NullPointerException exception = new NullPointerException("User entity is null");
        when(userService.findById(USER_ID)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> userController.getNote(USER_ID))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User entity is null");
        verify(userService).findById(USER_ID);
    }

    @Test
    @DisplayName("""
            GIVEN mapper throws exception during request conversion
            WHEN creating user with invalid request
            THEN exception is propagated
            """)
    void shouldNotCallServiceWhenMapperThrowsException() {
        // Given
        UserRequest userRequest = UserRequest.builder().email(USER_EMAIL).build();
        IllegalArgumentException exception = new IllegalArgumentException("Mapping failed");
        when(mapper.toUser(userRequest)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> userController.create(userRequest, httpServletRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mapping failed");
    }

    @Test
    @DisplayName("""
            GIVEN mapper throws exception during response conversion
            WHEN user found but cannot be converted to response
            THEN exception is propagated
            """)
    void shouldPropagateExceptionWhenMapperFailsOnResponse() {
        // Given
        User user = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .createdAt(CREATED_AT)
                .build();
        IllegalArgumentException exception = new IllegalArgumentException("Cannot map to response");
        when(userService.findById(USER_ID)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> userController.getNote(USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot map to response");
        verify(userService).findById(USER_ID);
        verify(mapper).toUserResponse(user);
    }

    @Test
    @DisplayName("""
            GIVEN user service throws UnsupportedOperationException
            WHEN performing unsupported operation
            THEN exception is propagated
            """)
    void shouldPropagateUnsupportedOperationException() {
        // Given
        UnsupportedOperationException exception = new UnsupportedOperationException("Bulk delete not supported");
        doThrow(exception).when(userService).deleteUser(USER_ID);

        // When & Then
        assertThatThrownBy(() -> userController.deleteNote(USER_ID))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Bulk delete not supported");
        verify(userService).deleteUser(USER_ID);
    }
}

