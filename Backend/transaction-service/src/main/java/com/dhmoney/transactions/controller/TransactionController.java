package com.dhmoney.transactions.controller;

import com.dhmoney.transactions.domain.model.Transaction;
import com.dhmoney.transactions.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionPublicDTO;
import com.dhmoney.transactions.exception.ResourceEmptyField;
import com.dhmoney.transactions.exception.ResourceNotFoundException;
import com.dhmoney.transactions.service.ITransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/transactions")
@AllArgsConstructor
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")

public class TransactionController {

    private final ITransaction service;

    @GetMapping
    @Operation(summary = "Get all transactions")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of all transactions found in database", content = @Content(schema = @Schema(implementation = TransactionDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
    public ResponseEntity<List<TransactionPublicDTO>> getTransactions(
            @RequestParam(name = "account", required = false) Long account_id,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "flow", required = false) String flow,
            @RequestParam(name = "monto_min", required = false) Double min_amount,
            @RequestParam(name = "monto_max", required = false) Double max_amount,
            @RequestParam(name = "fecha_min", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date min_date,
            @RequestParam(name = "fecha_max", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date max_date,
            @RequestParam(name = "limit", required = false) Integer limit) {

            return ResponseEntity.ok(service.getTransactions(account_id, type, flow, min_amount, max_amount, min_date, max_date, limit));
    }

    @PostMapping
    @Operation(summary = "Create a new transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public ResponseEntity<TransactionDTO> saveTransaction(@RequestBody TransactionDTO transaction) throws ResourceNotFoundException, ResourceEmptyField {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTransaction(transaction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transactions by id")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of transactions associated to an account id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))})
    @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "text/plain")})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "text/plain")})
    public TransactionPublicDTO getById(@PathVariable Long id, @RequestParam("account") Long account_id) throws ResourceNotFoundException {
        return service.getTransaction(id, account_id);
    }

    @GetMapping("/related_accounts")
    public ResponseEntity<List<RelatedAccountDTO>> getRelatedAccounts(@RequestParam(name = "account", required = true) Long account_id) {
        return ResponseEntity.ok(service.getRelatedAccounts(account_id));
    }

}