package com.anton.tsarenko.payment.core.api.users.controller;

import com.anton.tsarenko.payment.core.api.users.dto.UserRequest;
import com.anton.tsarenko.payment.core.api.users.dto.UserResponse;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import com.anton.tsarenko.payment.core.api.users.mapper.UserMapper;
import com.anton.tsarenko.payment.core.api.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing User entities.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    /**
     * POST /api/users handler for creating users.
     *
     * @param userRequest - request, containing user data
     * @param httpServletRequest - details of the request
     * @return {@link ResponseEntity} with URI of created user
     */
    @PostMapping
    public ResponseEntity<URI> create(
            @Valid @RequestBody UserRequest userRequest,
            HttpServletRequest httpServletRequest
    ) {
        User user = mapper.toUser(userRequest);
        return ResponseEntity.created(
                URI.create(
                        httpServletRequest.getRequestURI()
                                + "/"
                                + userService.createUser(user)
                )
        ).build();
    }

    /**
     * GET /api/users/{id} handler for retrieving a user by ID.
     *
     * @param id - the ID of the user to retrieve
     * @return {@link ResponseEntity} with the user data
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getNote(@PathVariable @Positive long id) {
        return ResponseEntity.ok(mapper.toUserResponse(userService.findById(id)));
    }

    /**
     * PUT /api/users/{id} handler for updating a user by ID.
     *
     * @param id - the ID of the user to update
     * @param userRequest - request, containing updated user data
     * @return {@link ResponseEntity} with no content
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNote(
            @PathVariable @Positive long id,
            @Valid @RequestBody UserRequest userRequest
    ) {
        userService.replaceUser(id, mapper.toUser(userRequest));
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/users/{id} handler for deleting a user by ID.
     *
     * @param id - the ID of the user to delete
     * @return {@link ResponseEntity} with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

