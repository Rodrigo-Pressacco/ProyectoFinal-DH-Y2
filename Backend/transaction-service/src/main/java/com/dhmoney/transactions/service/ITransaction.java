package com.dhmoney.transactions.service;

import com.dhmoney.transactions.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionDTO;
import com.dhmoney.transactions.domain.model.dto.TransactionPublicDTO;
import com.dhmoney.transactions.exception.ResourceEmptyField;
import com.dhmoney.transactions.exception.ResourceNotFoundException;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITransaction {
    List<TransactionPublicDTO> getTransactions(Long accountId, String type, String flow, Double min_amount, Double max_amount,
                                         Date min_date, Date max_date, Integer limit);
    TransactionDTO saveTransaction(TransactionDTO transaction) throws ResourceNotFoundException, ResourceEmptyField;
    TransactionPublicDTO getTransaction(Long id, Long accountId) throws ResourceNotFoundException;
    List<RelatedAccountDTO> getRelatedAccounts(Long accountId);
    //void deleteTransaction(Long id);
}
