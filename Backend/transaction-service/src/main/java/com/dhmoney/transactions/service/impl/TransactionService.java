package com.dhmoney.transactions.service.impl;

import com.dhmoney.transactions.domain.model.Transaction;
import com.dhmoney.transactions.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionPublicDTO;
import com.dhmoney.transactions.domain.repository.TransactionRepository;
import com.dhmoney.transactions.exception.ResourceEmptyField;
import com.dhmoney.transactions.exception.ResourceNotFoundException;
import com.dhmoney.transactions.service.ITransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService implements ITransaction {

    private final TransactionRepository repository;
    private final ModelMapper modelMapper;
    private final ObjectMapper mapper;

    @Transactional
    @Override
    public List<TransactionPublicDTO> getTransactions(Long accountId, String type, String flow, Double min_amount, Double max_amount,
                                                Date min_date, Date max_date, Integer limit) {
        log.info("Retrieving filtered transactions for account with ID " + accountId);
        List<Transaction> transactions = repository.getFilteredTransactions(accountId, type, flow, min_amount, max_amount, min_date, max_date, limit);

        List<TransactionPublicDTO> transactionPublicDTOS = new ArrayList<TransactionPublicDTO>();
        for(Transaction transaction : transactions) {
            transactionPublicDTOS.add(mapFromTransactionForAccount(accountId, transaction));
        }
        return transactionPublicDTOS;
    }

    @Override
    public TransactionDTO saveTransaction(TransactionDTO transactionDTO) throws ResourceEmptyField {
        validateEmptity(transactionDTO);
        Transaction transaction = mapper.convertValue(transactionDTO, Transaction.class);
        log.info("Saving transaction to local DB");
        Transaction transactionSaved = repository.save(transaction);
        log.info("A transaction with ID " + transactionSaved.getId() + " was created.");

        return modelMapper.map(transactionSaved, TransactionDTO.class);
    }

    @Override
    public TransactionPublicDTO getTransaction(Long id, Long accountId) throws ResourceNotFoundException {
        log.info("Retrieving transaction with ID " + id + " for account with ID " + accountId);
        Transaction transaction = repository.findByAccountAndId(accountId, id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction with ID " + id + " doesn't exist for this Account");
        }
        return mapFromTransactionForAccount(accountId, transaction);
    }

    public void validateEmptity(TransactionDTO transactionDTO) throws ResourceEmptyField {
        log.info("Validting empty fields for transaction");
        //Empty validations
        if (transactionDTO.getAmount() == null){
            throw new ResourceEmptyField("Amount field cannot be empty");
        }

        if(transactionDTO.getType() == null){
            throw new ResourceEmptyField("Type field cannot be empty");
        }
    }

    @Override
    public List<RelatedAccountDTO> getRelatedAccounts(Long accountId) {
        log.info("Retrieving related accounts for account with ID " + accountId);
        List<Tuple> tuples = repository.getRelatedAccounts(accountId);
        List<RelatedAccountDTO> relatedAccounts = new ArrayList<RelatedAccountDTO>();
        tuples.forEach( tuple -> {
            RelatedAccountDTO relatedAccount = new RelatedAccountDTO();
            relatedAccount.setName( (String) tuple.get("name") );
            relatedAccount.setCvu( (String) tuple.get("cvu") );
            relatedAccount.setLastInteractionDate( (Date) tuple.get("last_interaction_date") );
            relatedAccounts.add(relatedAccount);
        });

        return relatedAccounts;
    }

    private TransactionPublicDTO mapFromTransactionForAccount(Long accountId, Transaction transaction) {
        TransactionPublicDTO transactionPublicDTO = new TransactionPublicDTO();
        transactionPublicDTO.setId(transaction.getId());
        transactionPublicDTO.setDated(transaction.getDated());
        transactionPublicDTO.setType(transaction.getType());
        transactionPublicDTO.setDescription(transaction.getDescription());
        transactionPublicDTO.setDestination(transaction.getDestination());
        transactionPublicDTO.setOrigin(transaction.getOrigin());

        if (transactionPublicDTO.getType().equals("Deposit")) {
            transactionPublicDTO.setAmount(transaction.getAmount());
            transactionPublicDTO.setName(transaction.getNameOrigin());
        } else if (transaction.getAccountId().equals(accountId)) {
            // Es un TRANSFER_OUT
            transactionPublicDTO.setAmount(transaction.getAmount() * -1);
            transactionPublicDTO.setName(transaction.getNameDestination());
        } else {
            // Es un TRANSFER_IN
            transactionPublicDTO.setAmount(transaction.getAmount());
            transactionPublicDTO.setName(transaction.getNameOrigin());
        }

        return transactionPublicDTO;
    }

}