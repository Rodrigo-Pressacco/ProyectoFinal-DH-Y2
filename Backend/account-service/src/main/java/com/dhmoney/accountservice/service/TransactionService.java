package com.dhmoney.accountservice.service;

import com.dhmoney.accountservice.client.CardClient;
import com.dhmoney.accountservice.client.TransactionClient;
import com.dhmoney.accountservice.domain.model.Transaction;
import com.dhmoney.accountservice.domain.model.dto.AccountDTO;
import com.dhmoney.accountservice.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.accountservice.domain.model.dto.card.CardPublicDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionPublicDTO;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ResourceEmptyField;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.dhmoney.accountservice.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

    TransactionClient transactionClient;
    CardClient cardClient;
    AccountService accountService;
    Validator validator;
    ObjectMapper mapper;
    @Autowired
    private final ModelMapper modelMapper;

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public List<TransactionPublicDTO> getTransactions(Long accountId, String type, String flow, Double min_amount, Double max_amount,
                                                Date min_date, Date max_date, Integer limit) throws ResourceNotFoundException {
        log.info("Searching filtered transactions for account with ID " + accountId);
        validateAccount(accountId);
        return transactionClient.getTransactions(accountId, type, flow, min_amount, max_amount, min_date, max_date, limit).getBody();
    }

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public TransactionPublicDTO getTransactionById(Long accountId, Long transactionId) throws ResourceNotFoundException {
        log.info("Searching transaction with ID " + transactionId + " for account with ID " + accountId);
        validateAccount(accountId);
        return transactionClient.getById(transactionId, accountId).getBody();
    }

    public TransactionDTO createTransaction(Long id, TransactionDTO transactionDTO)
            throws ResourceNotFoundException, ResourceBadRequestException, ResourceEmptyField {
        AccountDTO account = validateAccount(id);

        log.info("Validating transaction type");
        if (transactionDTO.getType() == null) {
            throw new ResourceBadRequestException("The transference type is required");
        }

        AccountDTO sender = null;
        AccountDTO receiver = null;
        if (transactionDTO.getType().equals("Deposit")) {
            log.info("Creating a deposit for account with ID " + id);
            validateDepositStructure(id, transactionDTO);
            log.info("Searching card with ID " + transactionDTO.getOrigin() + " for account with ID " + id);
            CardPublicDTO cardPublicDTO = cardClient.getById(Long.parseLong(transactionDTO.getOrigin()), id).getBody();
            //The destination of the deposit will always be the cvu of the account
            transactionDTO.setDestination(account.getCvu());
            Long cardID = Long.valueOf(transactionDTO.getOrigin());
            transactionDTO.setCardID(cardID);
            transactionDTO.setOrigin(null);
            transactionDTO.setNameOrigin(cardPublicDTO.getFullName());
            transactionDTO.setAccountId(null);
            transactionDTO.setAccountIdDestination(id);
            transactionDTO.setNameDestination(account.getName());

        } else if (transactionDTO.getType().equals("Transfer")) {
            log.info("Creating a transfer for account with ID " + id);
            validateStructure(transactionDTO);
            log.info("Retrieving account with ID " + id);
            sender = accountService.findById(id);
            log.info("Retrieving account with CVU " + transactionDTO.getDestination());
            receiver = accountService.findByCVU(transactionDTO.getDestination());

            transactionDTO.setOrigin(sender.getCvu());
            transactionDTO.setNameOrigin(sender.getName());
            transactionDTO.setAccountId(id);
            transactionDTO.setAccountIdDestination(receiver.getId());
            transactionDTO.setNameDestination(receiver.getName());

            log.info("Validating account balance");
            if (sender.getBalance() < transactionDTO.getAmount()){
                throw new ResourceBadRequestException("Insufficient account balance");
            }
        } else {
            throw new ResourceBadRequestException("The specified transference type doesn't exist");
        }

        transactionDTO.setDated(Date.from(Instant.now()));

        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        log.info("Saving transaction for account with ID " + id);
        ResponseEntity<TransactionDTO> responseTransaction = transactionClient.saveTransaction(transaction);

        log.info("Updating accounts with new balances");
        if (transaction.getType().equals("Deposit")) {
            accountService.setBalance(account.getCvu(),
                    account.getBalance() + transactionDTO.getAmount());
        } else {
            accountService.setBalance(sender.getCvu(), sender.getBalance() - transactionDTO.getAmount());
            accountService.setBalance(receiver.getCvu(), receiver.getBalance() + transactionDTO.getAmount());
        }

        return responseTransaction.getBody();
    }

    @CircuitBreaker(name = "circuitbreaker_config", fallbackMethod = "getRelatedAccountsFallback")
    @Retry(name = "retry_config")
    public List<RelatedAccountDTO> getRelatedAccounts(Long accountId) throws ResourceNotFoundException {
        log.info("Searching related accounts for account with ID " + accountId);
        validateAccount(accountId);
        return transactionClient.getRelatedAccounts(accountId).getBody();
    }

    private AccountDTO validateAccount(Long id) throws ResourceNotFoundException {
        log.info("Searching for account with ID " + id);
        AccountDTO account = accountService.findById(id);
        if (account == null) {
            throw new ResourceNotFoundException("Account with ID " + id + " doesn't exist");
        }

        return account;
    }

    private void validateDepositStructure(Long id,
                                          TransactionDTO transaction) throws ResourceBadRequestException, ResourceNotFoundException {
        log.info("Validating deposit fields");
        if (transaction.getAmount() == null || (Double.compare(transaction.getAmount(), 0) == 0)) {
            throw new ResourceBadRequestException("Amount field cannot be zero or empty");
        }

        if (transaction.getAmount() < 0) {
            throw new ResourceBadRequestException("Amount field cannot be negative");
        }

        simulateCardBalanceValidation(transaction.getAmount());

        if (!validator.validateDescription(transaction.getDescription())) {
            throw new ResourceBadRequestException("The description is " +
                    "invalid");
        }

        // Validacion de que existe la tarjeta para esta cuenta (tira excepcion si no existe)
        // Comentado porque se hace en createTransaction()
        //cardClient.getById(Long.parseLong(transaction.getOrigin()), id);
    }

    private void simulateCardBalanceValidation(Double amount) throws ResourceBadRequestException {
        log.info("Validating card balance");
        int randomProbability = ThreadLocalRandom.current().nextInt(0, 100);
        if (    amount  < 1000 && randomProbability >= 98 ||
                amount >= 1000 && amount  < 2000 && randomProbability >= 95 ||
                amount >= 2000 && amount  < 5000 && randomProbability >= 90 ||
                amount >= 5000 && amount  < 10000 && randomProbability >= 75 ||
                amount >= 10000 && amount  < 20000 && randomProbability >= 60 ||
                amount >= 20000 && amount  < 50000 && randomProbability >= 40 ||
                amount >= 50000 && amount  < 100000 && randomProbability >= 25 ||
                amount >= 100000 && amount  < 250000 && randomProbability >= 10 ||
                amount  >= 250000 && randomProbability >= 2) {
            throw new ResourceBadRequestException("Insufficient card balance");
        }
    }

    private void validateStructure(TransactionDTO transaction) throws ResourceBadRequestException {
        log.info("Validating transaction fields");
        if (transaction.getAmount() == null || (Double.compare(transaction.getAmount(), 0) == 0)) {
            throw new ResourceBadRequestException("Amount field cannot be zero or empty");
        }

        if (!validator.isNumber(transaction.getDestination())) {
            throw new ResourceBadRequestException("The destination CVU is " +
                    "invalid");
        }
    }


    // Fallbacks

    private List<RelatedAccountDTO>  getRelatedAccountsFallback(Long accountId, CallNotPermittedException exception) {
        return new ArrayList<RelatedAccountDTO>();
    }

}
