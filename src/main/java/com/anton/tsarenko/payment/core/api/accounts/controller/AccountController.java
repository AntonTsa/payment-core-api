package com.anton.tsarenko.payment.core.api.accounts.controller;

import com.anton.tsarenko.payment.core.api.accounts.dto.AccountRequest;
import com.anton.tsarenko.payment.core.api.accounts.dto.AccountResponse;
import com.anton.tsarenko.payment.core.api.accounts.entity.Account;
import com.anton.tsarenko.payment.core.api.accounts.mapper.AccountMapper;
import com.anton.tsarenko.payment.core.api.accounts.service.AccountService;
import com.anton.tsarenko.payment.core.api.shared.api.response.RestContractExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
 * Controller class for managing Account entities.
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Accounts")
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper mapper;

    /**
     * POST /api/accounts handler for creating accounts.
     *
     * @param accountRequest - request, containing account data
     * @param httpServletRequest - details of the request
     * @return {@link ResponseEntity} with URI of created account
     */
    @PostMapping
    @Operation(summary = "Create account", description = "Creates a new account for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account request",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<URI> create(
            @Valid @RequestBody AccountRequest accountRequest,
            HttpServletRequest httpServletRequest
    ) {
        Account account = mapper.toAccount(accountRequest);
        return ResponseEntity.created(
                URI.create(
                        httpServletRequest.getRequestURI()
                                + "/"
                                + accountService.createAccount(account)
                )
        ).build();
    }

    /**
     * GET /api/accounts/{id} handler for retrieving an account by ID.
     *
     * @param id - the ID of the account to retrieve
     * @return {@link ResponseEntity} with the account data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get account", description = "Returns an account by identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account identifier",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountResponse> get(
            @Parameter(
                    description = "Account id.",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(mapper.toAccountResponse(accountService.findById(id)));
    }

    /**
     * PUT /api/accounts/{id} handler for updating an account by ID.
     *
     * @param id - the ID of the account to update
     * @param accountRequest - request, containing updated account data
     * @return {@link ResponseEntity} with no content
     */
    @PutMapping("/{id}")
    @Operation(summary = "Replace account", description = "Replaces mutable account fields by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account replaced"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account request",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> update(
            @Parameter(
                    description = "Account id.",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest accountRequest
    ) {
        accountService.replaceAccount(id, mapper.toAccount(accountRequest));
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/accounts/{id} handler for deleting an account by ID.
     *
     * @param id - the ID of the account to delete
     * @return {@link ResponseEntity} with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Deletes an account by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account identifier",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(schema = @Schema(implementation = RestContractExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Account id.",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
