package com.anton.tsarenko.payment.core.api.users.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;

/**
 * DTO for response with exception.
 *
 * @param timestamp - timestamp of exception
 * @param error - type of exception
 * @param message - message of exception
 */
@Builder
public record RestContractExceptionResponse(
        LocalDateTime timestamp,
        String error,
        String message
) implements Serializable {}
