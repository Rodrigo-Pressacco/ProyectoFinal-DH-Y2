package com.dhmoney.accountservice.controller;

import com.dhmoney.accountservice.domain.model.dto.AccountDTO;
import com.dhmoney.accountservice.domain.model.dto.AccountNameDTO;
import com.dhmoney.accountservice.domain.model.dto.AliasDTO;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ForbiddenException;
import com.dhmoney.accountservice.exception.ResourceConflictException;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.dhmoney.accountservice.service.AccountService;
import com.dhmoney.accountservice.utils.SecurityValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")
public class AccountController {

    private final AccountService service;
    private final SecurityValidator securityValidator;

    @GetMapping("/{user}")
    @Operation(summary = "Get Account by user")
    @ApiResponse(responseCode = "200", description = "Account found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Account not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<AccountDTO> getByUser(@PathVariable Long user) throws ResourceNotFoundException {
        AccountDTO account = service.getAccountByUser(user);
        return ResponseEntity.ok().body(account);
    }

    @GetMapping
    @Operation(summary = "Get accounts")
    @ApiResponse(responseCode = "200", description = "Account found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Account not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @PostMapping("/{user}")
    @Operation(summary = "Create account")
    @ApiResponse(responseCode = "201", description = "Account created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request Error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "User forbidden", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<AccountDTO> createAccount(@PathVariable Long user,
                                                    @RequestBody AccountNameDTO accountNameDTO, Authentication authentication)
            throws ForbiddenException {

        if(!securityValidator.isUserAnotherService(authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        AccountDTO account = service.createAccount(user, accountNameDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PatchMapping("/{user}")
    @Operation(summary = "Patch update an account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Account updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Account not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<AccountDTO> patchAccount(@PathVariable Long user,
                                                   @RequestBody AliasDTO aliasDTO,
                                                   Authentication authentication) throws ForbiddenException,
            ResourceConflictException, ResourceBadRequestException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(user, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        AccountDTO accountDTO = service.patchAccount(user,
                aliasDTO.getAlias());
        return ResponseEntity.ok().body(accountDTO);
    }

    @PatchMapping("/name/{user}")
    public ResponseEntity<AccountDTO> updateUserName(@PathVariable Long user,
                                                     @RequestBody AccountNameDTO accountNameDTO,
                                                     Authentication authentication) throws ForbiddenException {

        if(!securityValidator.isUserAnotherService(authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.ok().body(service.updateUserName(user, accountNameDTO));
    }
}
