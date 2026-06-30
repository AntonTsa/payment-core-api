package com.anton.tsarenko.payment.core.api.users.controller;

import com.anton.tsarenko.payment.core.api.users.dto.RestContractExceptionResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handling controller.
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(basePackages = {"com.anton.tsarenko.payment.core.api.users.controller"})
public class GlobalExceptionHandler {

    private static final String REASON_DELIMITER = ": ";
    private static final String MULTIPLE_ERRORS_DELIMITER = ", ";

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_MAPPING = Map.of(
            HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE
    );

    /**
     * Handler for HTTP status code exceptions.
     *
     * @param exception {@link HttpStatusCodeException} thrown by service
     * @return {@link ResponseEntity} with status, header and body
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleBindException(
            HttpStatusCodeException exception
    ) {
        return map(Objects.requireNonNull(HttpStatus.resolve(exception.getStatusCode().value())),
                exception.getStatusText(), exception);
    }

    /**
     * Exception handler for exceptions related to params.
     *
     * @param exception {@link MethodArgumentNotValidException}
     *                  to catch and extract error messages from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleBindException(
            MethodArgumentNotValidException exception
    ) {
        return map(
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError ->
                                fieldError.getField()
                                        + Optional.ofNullable(fieldError.getDefaultMessage())
                                        .map(REASON_DELIMITER::concat)
                                        .orElse("")
                        )
                        .distinct()
                        .sorted(),
                exception
        );
    }

    /**
     * Exception handler for empty results in storage.
     *
     * @param exception {@link EmptyResultDataAccessException}
     *                  to catch and extract error messages from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#NOT_FOUND},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleBindException(
            EmptyResultDataAccessException exception
    ) {
        return map(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.name(),
                exception
        );
    }

    /**
     * Method argument mismatch exception handler.
     *
     * @param exception {@link MethodArgumentTypeMismatchException}
     *                  to catch and extract meaningful response
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST}
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleBindException(
            MethodArgumentTypeMismatchException exception
    ) {
        return map(
                Stream.concat(
                        Stream.of(exception.getName() + REASON_DELIMITER + "provided wrong type"),
                        Stream.ofNullable(exception.getRequiredType())
                                .map(Class::getSimpleName)
                                .map("expected type is "::concat)
                ),
                exception
        );
    }


    /**
     * Not readable request data exception handler.
     *
     * @param exception {@link HttpMessageNotReadableException}
     *                  to catch and extract meaningful response
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleBindException(
            HttpMessageNotReadableException exception
    ) {
        return map(HttpStatus.BAD_REQUEST, "Invalid request body received", exception);
    }

    /**
     * Global exception Handler.
     *
     * @param exception - generic exception that is thrown.
     * @return {@link ResponseEntity} with status and body
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<RestContractExceptionResponse> handleThrowable(Throwable exception) {
        return map(
                EXCEPTION_MAPPING
                        .getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR),
                exception.getMessage(),
                exception
        );
    }

    /**
     * Converts specific exceptions to meaningful response.
     *
     * @param messages  stream of messages
     * @param throwable cause
     * @return {@link ResponseEntity} with status, header and body
     */
    private ResponseEntity<RestContractExceptionResponse> map(Stream<String> messages,
                                                              Throwable throwable) {
        return map(HttpStatus.BAD_REQUEST, messages.collect(
                Collectors.joining(MULTIPLE_ERRORS_DELIMITER)), throwable);
    }

    /**
     * Converts specific exceptions to meaningful response.
     *
     * @param httpStatus generic error code
     * @param message    descriptive message of an error
     * @param throwable  cause
     * @return {@link ResponseEntity} with status, header and body
     */
    private ResponseEntity<RestContractExceptionResponse> map(
            HttpStatus httpStatus,
            String message,
            Throwable throwable) {
        var reasonPhrase = httpStatus.getReasonPhrase();
        log.error("Error: {}, Message: {}, Cause: {}", reasonPhrase, message, throwable.toString());
        return ResponseEntity.status(httpStatus)
                .body(RestContractExceptionResponse.builder()
                        .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                        .error(reasonPhrase)
                        .message(message)
                        .build());
    }

}
