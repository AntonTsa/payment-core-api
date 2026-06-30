package com.anton.tsarenko.payment.core.api.users.mapper;

import com.anton.tsarenko.payment.core.api.users.dto.UserRequest;
import com.anton.tsarenko.payment.core.api.users.dto.UserResponse;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entities and DTOs.
 */
@Component
public class UserMapper {
    /**
     * Convert a UserRequest DTO to a User entity.
     *
     * @param request - the UserRequest DTO containing user data
     * @return - User entity
     */
    public User toUser(UserRequest request) {
        return User.builder()
                .email(request.email())
                .build();
    }

    /**
     * Convert a User entity to a UserResponse DTO.
     *
     * @param user - the User entity
     * @return - UserResponse DTO
     */
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
