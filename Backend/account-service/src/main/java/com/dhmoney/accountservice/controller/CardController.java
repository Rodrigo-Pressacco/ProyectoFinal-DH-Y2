package com.dhmoney.accountservice.controller;

import com.dhmoney.accountservice.domain.model.dto.card.CardDTO;
import com.dhmoney.accountservice.domain.model.dto.card.CardPublicDTO;
import com.dhmoney.accountservice.exception.*;
import com.dhmoney.accountservice.service.CardService;
import com.dhmoney.accountservice.utils.JsonResponseMessage;
import com.dhmoney.accountservice.utils.SecurityValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")
public class CardController {

    @Autowired
    private final CardService cardService;
    @Autowired
    private final SecurityValidator securityValidator;

    @PostMapping("/{id}/cards")
    @Operation(summary = "Associate a card to an account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Account found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardDTO.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Account not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "409", description = "Card already associated with account", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    public ResponseEntity<CardPublicDTO> saveCard(@PathVariable Long id, @RequestBody CardDTO cardDTO,
                                         Authentication authentication) throws ForbiddenException, ResourceNotFoundException,
            ResourceConflictException, ResourceBadRequestException, ResourceEmptyField {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.saveCard(id, cardDTO));
    }

    @DeleteMapping("/{accountId}/cards/{id}")
    @Operation(summary = "Delete card by id and account id")
    @ApiResponse(responseCode = "200", description = "Card delete", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteCard(@PathVariable Long accountId, @PathVariable Long id,
                                        Authentication authentication) throws ForbiddenException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(accountId, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        cardService.deleteCard(id, accountId);
        return ResponseEntity.ok(new JsonResponseMessage("The card has been deleted"));
    }

    @GetMapping("/{id}/cards")
    @Operation(summary = "Get cards by account id")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Found cards", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardPublicDTO.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<List<CardPublicDTO>> getCardsByAccountId(@PathVariable Long id, Authentication authentication)
            throws ResourceNotFoundException, ForbiddenException {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.ok(cardService.getCardsByAccountID(id));
    }

    @GetMapping("/{accountId}/cards/{id}")
    @Operation(summary = "Get card by id and account id")
    @ApiResponse(responseCode = "200", description = "Card found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardPublicDTO.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CardPublicDTO> getCardByAccountIdAndCardID(@PathVariable Long accountId, @PathVariable Long id, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(accountId, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.ok(cardService.getById(id, accountId));
    }


}
