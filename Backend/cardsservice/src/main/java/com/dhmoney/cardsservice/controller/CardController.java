package com.dhmoney.cardsservice.controller;

import com.dhmoney.cardsservice.domain.model.dto.CardDTO;
import com.dhmoney.cardsservice.domain.model.dto.CardPublicDTO;
import com.dhmoney.cardsservice.exception.*;
import com.dhmoney.cardsservice.service.ICardService;
import com.dhmoney.cardsservice.utils.JsonResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")
public class CardController {

  private ICardService cardService;

  @Autowired
  public CardController(ICardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping("/{id}/account/{accountId}")
  @Operation(summary = "Get card by id and account id")
  @ApiResponse(responseCode = "200", description = "Card found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardPublicDTO.class))})
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<CardPublicDTO> getCardById(@PathVariable Long id,
                                @PathVariable Long accountId) throws ResourceNotFoundException {
    return ResponseEntity.ok(cardService.getCardById(id, accountId));
  }

  @GetMapping("/account/{accountId}")
  @Operation(summary = "Get cards by account id")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponse(responseCode = "200", description = "Found cards", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardPublicDTO.class))})
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  public ResponseEntity<List<CardPublicDTO>> getCardsByAccountId(@PathVariable Long accountId) {
    return ResponseEntity.ok(cardService.getAllCardsByAccount(accountId));
  }

  @PostMapping
  @Operation(summary = "Create a card")
  @ApiResponse(responseCode = "201", description = "Card created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CardDTO.class))})
  @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @ApiResponse(responseCode = "409", description = "Conflict", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<CardPublicDTO> saveCard(@RequestBody CardDTO cardDTO)
          throws ResourceConflictException, ResourceBadRequestException, ResourceEmptyField {
    CardPublicDTO cardPublicDTO = cardService.saveCard(cardDTO);
    if (cardPublicDTO != null) {
      return ResponseEntity.status(HttpStatus.CREATED).body(cardPublicDTO);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}/account/{accountId}")
  @Operation(summary = "Delete card by id and account id")
  @ApiResponse(responseCode = "200", description = "Card delete", content = @Content)
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @ApiResponse(responseCode = "404", description = "Card not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> deleteCard(@PathVariable Long id, @PathVariable Long accountId)
          throws  ResourceNotFoundException {
    cardService.deleteCard(id, accountId);
    return ResponseEntity.ok(new JsonResponseMessage("The card has been deleted"));
  }

}