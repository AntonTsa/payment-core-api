package com.anton.tsarenko.payment.core.api.users.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing body of response.
 *
 * @param id - user's id
 * @param email - user's email
 * @param createdAt - timestamp of user creation
 */
@Builder
@Jacksonized
public record UserResponse(
        Long id,
        String email,
        Instant createdAt
) implements Serializable {}
