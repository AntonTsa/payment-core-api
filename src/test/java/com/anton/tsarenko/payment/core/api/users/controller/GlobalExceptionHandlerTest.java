package com.anton.tsarenko.payment.core.api.users.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.payment.core.api.shared.api.exceptions.GlobalExceptionHandler;
import com.anton.tsarenko.payment.core.api.shared.api.response.RestContractExceptionResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("""
            GIVEN an HttpStatusCodeException with 404 status
            WHEN handling the exception
            THEN response contains 404 status with reason phrase and message
            """)
    void shouldHandleHttpStatusCodeException() {
        // Given
        HttpStatusCodeException exception = new HttpStatusCodeException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Not Found") {
        };

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("""
            GIVEN an HttpStatusCodeException with 500 status and custom message
            WHEN handling the exception
            THEN response has 500 status and preserves the message
            """)
    void shouldPreserveStatusAndMessageForHttpStatusCodeException() {
        // Given
        HttpStatusCodeException exception = new HttpStatusCodeException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error") {
        };

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Internal Server Error");
    }

    @Test
    @DisplayName("""
            GIVEN an EmptyResultDataAccessException
            WHEN handling the exception
            THEN response has 404 status with NOT_FOUND reason phrase
            """)
    void shouldHandleEmptyResultDataAccessException() {
        // Given
        EmptyResultDataAccessException exception = new EmptyResultDataAccessException(1);

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("""
            GIVEN an HttpMessageNotReadableException
            WHEN handling the exception
            THEN response has 400 status with "Invalid request body received" message
            """)
    void shouldHandleHttpMessageNotReadableException() {
        // Given
        HttpInputMessage mockInputMessage = Mockito.mock(HttpInputMessage.class);
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("Invalid JSON", mockInputMessage);

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid request body received");
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("""
            GIVEN an HttpMediaTypeNotSupportedException
            WHEN handling the exception with global handler
            THEN response has 415 status code
            """)
    void shouldHandleHttpMediaTypeNotSupportedExceptionViaThrowableHandler() {
        // Given
        HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException("application/xml");

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleThrowable(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("""
            GIVEN an unexpected generic Exception
            WHEN handling via global throwable handler
            THEN response has 500 status with exception message
            """)
    void shouldHandleGenericThrowableWithInternalServerError() {
        // Given
        Exception exception = new Exception("Something went wrong");

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleThrowable(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
    }

    @Test
    @DisplayName("""
            GIVEN any exception with response body
            WHEN handling the exception
            THEN response body contains non-null timestamp in UTC
            """)
    void shouldIncludeTimestampInResponseBody() {
        // Given
        Exception exception = new Exception("Test error");

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleThrowable(exception);
        LocalDateTime responseTimestamp = Objects.requireNonNull(response.getBody()).timestamp();

        // Then
        assertThat(responseTimestamp).isNotNull();
        LocalDateTime beforeTime = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        LocalDateTime afterTime = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);
        assertThat(responseTimestamp).isBetween(beforeTime, afterTime);
    }

    @Test
    @DisplayName("""
            GIVEN multiple different exceptions
            WHEN handling each one
            THEN each produces response with correct status code and error message
            """)
    void shouldHandleMultipleExceptionTypesCorrectly() {
        // Given
        EmptyResultDataAccessException notFoundException = new EmptyResultDataAccessException(1);
        HttpInputMessage mockInputMessage = Mockito.mock(HttpInputMessage.class);
        HttpMessageNotReadableException badRequestException =
                new HttpMessageNotReadableException("Invalid", mockInputMessage);
        Exception internalErrorException = new Exception("Internal error");

        // When
        ResponseEntity<RestContractExceptionResponse> response1 =
                handler.handleBindException(notFoundException);
        ResponseEntity<RestContractExceptionResponse> response2 =
                handler.handleBindException(badRequestException);
        ResponseEntity<RestContractExceptionResponse> response3 =
                handler.handleThrowable(internalErrorException);

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("""
            GIVEN an HttpStatusCodeException with 409 Conflict status
            WHEN handling the exception
            THEN response has correct conflict status and reason
            """)
    void shouldHandleConflictStatusException() {
        // Given
        HttpStatusCodeException exception = new HttpStatusCodeException(
                org.springframework.http.HttpStatus.CONFLICT, "Resource conflict") {
        };

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Conflict");
    }

    @Test
    @DisplayName("""
            GIVEN an exception with null message
            WHEN handling via global throwable handler
            THEN response has 500 status and null message in body
            """)
    void shouldHandleExceptionWithNullMessage() {
        // Given
        Exception exception = new Exception((String) null);

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleThrowable(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isNull();
    }

    @Test
    @DisplayName("""
            GIVEN an EmptyResultDataAccessException with expected row count
            WHEN handling the exception
            THEN response has 404 status and NOT_FOUND error message
            """)
    void shouldAlwaysReturn404ForEmptyResultException() {
        // Given
        EmptyResultDataAccessException exception1 = new EmptyResultDataAccessException(1);
        EmptyResultDataAccessException exception2 = new EmptyResultDataAccessException(5);

        // When
        ResponseEntity<RestContractExceptionResponse> response1 = handler.handleBindException(exception1);
        ResponseEntity<RestContractExceptionResponse> response2 = handler.handleBindException(exception2);

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response1.getBody()).isNotNull();
        assertThat(response2.getBody()).isNotNull();
        assertThat(response1.getBody().error()).isEqualTo("Not Found");
        assertThat(response2.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("""
            GIVEN an HttpStatusCodeException
            WHEN handling the exception
            THEN response body contains error field with reason phrase
            """)
    void shouldPopulateErrorFieldWithReasonPhrase() {
        // Given
        HttpStatusCodeException exception = new HttpStatusCodeException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Forbidden") {
        };

        // When
        ResponseEntity<RestContractExceptionResponse> response = handler.handleBindException(exception);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isNotBlank();
        assertThat(response.getBody().error()).isEqualTo("Forbidden");
    }
}
