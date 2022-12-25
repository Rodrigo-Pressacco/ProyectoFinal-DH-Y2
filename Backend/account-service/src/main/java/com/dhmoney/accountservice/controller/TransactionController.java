package com.dhmoney.accountservice.controller;

import com.dhmoney.accountservice.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionPublicDTO;
import com.dhmoney.accountservice.exception.ForbiddenException;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ResourceEmptyField;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.dhmoney.accountservice.service.TransactionService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;
    @Autowired
    private final SecurityValidator securityValidator;

    @GetMapping("/{id}/activities")
    @Operation(summary = "Get all activities")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of activities in the account", content = @Content(schema = @Schema(implementation = TransactionDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<List<TransactionPublicDTO>> getTransactions(
            @PathVariable(name = "id", required = false) Long account_id,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "flow", required = false) String flow,
            @RequestParam(name = "monto_min", required = false) Double min_amount,
            @RequestParam(name = "monto_max", required = false) Double max_amount,
            @RequestParam(name = "fecha_min", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date min_date,
            @RequestParam(name = "fecha_max", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date max_date,
            @RequestParam(name = "limit", required = false) Integer limit,
            Authentication authentication
    ) throws ForbiddenException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(account_id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.ok(transactionService.getTransactions(account_id, type, flow, min_amount, max_amount, min_date, max_date, limit));
    }

    @PostMapping("/{id}/activities")
    @Operation(summary = "Create a new transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public ResponseEntity<TransactionDTO> saveTransaction(@PathVariable Long id, @RequestBody TransactionDTO transaction)
            throws ResourceNotFoundException, ResourceBadRequestException, ResourceEmptyField {
        TransactionDTO transactionDTO = transactionService.createTransaction(id, transaction);
        if (transactionDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    /*@PostMapping("/{id}/transferences")
    public ResponseEntity<TransactionDTO> saveTransferences(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException, ResourceBadRequestException, ResourceEmptyField {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        transactionDTO.setType("Transfer");
        transactionDTO = transactionService.createTransaction(id, transactionDTO);
        if (transactionDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/deposits")
    @Operation(summary = "Create a new deposit")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "deposit created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public ResponseEntity<TransactionDTO> saveDeposits(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException, ResourceBadRequestException, ResourceEmptyField {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        transactionDTO.setType("Deposit");
        transactionDTO = transactionService.createTransaction(id,
                transactionDTO);
        if (transactionDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
        }
        return ResponseEntity.notFound().build();
    }*/

    @PostMapping("/{id}/transferences")
    @Operation(summary = "Create a new Transfer")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Transfer created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public ResponseEntity<TransactionDTO> saveTransferences(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException, ResourceBadRequestException, ResourceEmptyField {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        transactionDTO = transactionService.createTransaction(id,
                transactionDTO);
        if (transactionDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities/{transaction_id}")
    @Operation(summary = "Get a transaction")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Get transaction by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public ResponseEntity<TransactionPublicDTO> getTransactionById(@PathVariable Long id, @PathVariable Long transaction_id, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        TransactionPublicDTO transaction = transactionService.getTransactionById(id, transaction_id);
        if (transaction != null) {
            return ResponseEntity.ok().body(transaction);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/related_accounts")
    public ResponseEntity<List<RelatedAccountDTO>> getRelatedAccounts(@PathVariable Long id, Authentication authentication)
            throws ForbiddenException, ResourceNotFoundException {

        if(!securityValidator.isUserTheAccountOwner(id, authentication)) {
            throw new ForbiddenException("Access not allowed");
        }

        return ResponseEntity.ok(transactionService.getRelatedAccounts(id));
    }

}
