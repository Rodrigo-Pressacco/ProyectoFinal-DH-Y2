package com.dhmoney.accountservice.client;

import com.dhmoney.accountservice.config.feign.FeignClientConfiguration;
import com.dhmoney.accountservice.domain.model.dto.card.CardDTO;
import com.dhmoney.accountservice.domain.model.dto.card.CardPublicDTO;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ResourceConflictException;
import com.dhmoney.accountservice.exception.ResourceEmptyField;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "dm-cards", url = "http://localhost:8085/cards/", configuration = FeignClientConfiguration.class)
public interface CardClient {

    @PostMapping
    public ResponseEntity<CardPublicDTO> saveCard(@RequestBody CardDTO cardDTO) throws ResourceConflictException,
            ResourceBadRequestException, ResourceEmptyField;

    @GetMapping("{id}/account/{accountId}")
    public ResponseEntity<CardPublicDTO> getById(@PathVariable Long id, @PathVariable Long accountId) throws ResourceNotFoundException;

    @DeleteMapping("{id}/account/{accountId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id, @PathVariable Long accountId) throws ResourceNotFoundException;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardPublicDTO>> getCardsByAccountId(@PathVariable Long accountId);
}