package com.dhmoney.accountservice.service;

import com.dhmoney.accountservice.client.CardClient;
import com.dhmoney.accountservice.domain.model.dto.AccountDTO;
import com.dhmoney.accountservice.domain.model.dto.card.CardDTO;
import com.dhmoney.accountservice.domain.model.dto.card.CardPublicDTO;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ResourceConflictException;
import com.dhmoney.accountservice.exception.ResourceEmptyField;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {

    CardClient cardClient;
    AccountService accountService;

    public CardPublicDTO saveCard(Long accountId, CardDTO cardDTO) throws ResourceNotFoundException,
            ResourceConflictException, ResourceBadRequestException, ResourceEmptyField {
        log.info("Saving card for account with ID " + accountId);
        validateAccount(accountId);
        cardDTO.setAccountId(accountId);
        return cardClient.saveCard(cardDTO).getBody();
    }

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public List<CardPublicDTO> getCardsByAccountID(Long id) throws ResourceNotFoundException {
        log.info("Searching all cards for account with ID " + id);
        validateAccount(id);
        return cardClient.getCardsByAccountId(id).getBody();
    }

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public void deleteCard(Long id, Long accountId) throws ResourceNotFoundException {
        log.info("Deleting card with ID " + id + " for account with ID " + accountId);
        validateAccount(accountId);
        cardClient.deleteCard(id, accountId);
    }

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public CardPublicDTO getById(Long id, Long accountId) throws ResourceNotFoundException {
        log.info("Searching card with ID " + id + " for account with ID " + accountId);
        validateAccount(accountId);
        return cardClient.getById(id, accountId).getBody();
    }

    private boolean validateAccount(Long id) throws ResourceNotFoundException{
        AccountDTO account = accountService.findById(id);
        if (account == null)
            throw new ResourceNotFoundException("Account with ID " + id + " doesn't exist");
        //if (account.getId() != userId);
        // throw new ResourceUnAuth("Account doesn't exists");
        return true;
    }

}
