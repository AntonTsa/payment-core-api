package com.anton.tsarenko.payment.core.api.users.dto;

import jakarta.validation.constraints.Email;
import java.io.Serializable;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO, containing values from requests.
 *
 * @param email - user email in request
 */
@Builder
@Jacksonized
public record UserRequest(@Email String email) implements Serializable {}
