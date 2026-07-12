package com.anton.tsarenko.payment.core.api.users.controller;

import com.anton.tsarenko.payment.core.api.users.dto.RestContractExceptionResponse;
import com.anton.tsarenko.payment.core.api.users.dto.UserRequest;
import com.anton.tsarenko.payment.core.api.users.dto.UserResponse;
import com.anton.tsarenko.payment.core.api.users.entity.User;
import com.anton.tsarenko.payment.core.api.users.mapper.UserMapper;
import com.anton.tsarenko.payment.core.api.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users")
@RequestMapping("/api/v1/users")
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
    @Operation(summary = "Create user", description = "Creates a new user with a unique email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user request",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(summary = "Get user", description = "Returns a user by identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user identifier",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponse> get(
            @Parameter(description = "Positive user identifier.", example = "1")
            @PathVariable @Positive long id
    ) {
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
    @Operation(summary = "Replace user", description = "Replaces mutable user fields by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User replaced"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user request",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "Positive user identifier.", example = "1")
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
    @Operation(summary = "Delete user", description = "Deletes a user by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user identifier",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Positive user identifier.", example = "1")
            @PathVariable @Positive Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

