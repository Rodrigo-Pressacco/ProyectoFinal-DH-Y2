package com.dhmoney.userservice.controller;

import com.dhmoney.userservice.domain.model.User;
import com.dhmoney.userservice.domain.model.dto.*;
import com.dhmoney.userservice.exception.*;
import com.dhmoney.userservice.service.IUserService;
import com.dhmoney.userservice.service.impl.AccountService;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.MemberSubstitution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")

public class UserController {

    private IUserService userService;
    private AccountService accountService;

    @Autowired
    public UserController(IUserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "User verified", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountDTO.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<List<UserAccountDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a User")
    @ApiResponse(responseCode = "200", description = "User created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UserAccountDTO> saveUser(@RequestBody UserRegistrationDTO userRegistrationDTO)
            throws ResourceBadRequestException, ResourceConflictException, KeycloakErrorException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userRegistrationDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "User deleted...", content = @Content)
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<?> deleteById(@PathVariable Long id) throws ResourceNotFoundException, KeycloakErrorException {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted...");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "User found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
      public UserAccountDTO getById(@PathVariable Long id) throws ResourceNotFoundException {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Change user's parameters")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "User updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationDTO.class))})
    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public UserDTO patchByID(@PathVariable Long id, @RequestBody UserRegistrationDTO userRegistrationDTO) throws ResourceNotFoundException, ResourceConflictException, KeycloakErrorException, ResourceBadRequestException {
        return userService.patchByID(id, userRegistrationDTO);
    }

    @GetMapping("/{id}/iam")
    @Operation(summary = "Get User DB Keycloak")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "User found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<String> getIamIDByID(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserIamIDByID(id));
    }

    @GetMapping("/{id}/accounts")
    @Operation(summary = "Get User by Account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Account by user found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountFullDTO.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<AccountFullDTO> getFullAccountByUser(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getFullAccountByUser(id));
    }

    /*@PatchMapping("/{id}/accounts/{idAccount}")
    public ResponseEntity<AccountDTO> patchAccountAlias(@PathVariable Long id, @PathVariable Long idAccount,
                                                            @RequestBody AliasDTO aliasDTO) throws ResourceBadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.patchAccountAlias(id, aliasDTO));
    }*/
}
