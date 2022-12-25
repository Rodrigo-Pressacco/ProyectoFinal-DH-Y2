package com.dhmoney.transactions.domain.repository;

import com.dhmoney.transactions.domain.model.Transaction;
import com.dhmoney.transactions.domain.model.dto.RelatedAccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //FindAllByAccount
    @Query("SELECT t FROM Transaction t WHERE t.accountId = ?1 OR t.accountIdDestination = ?1")
    List<Transaction> findAllByAccount(Long accountId);

    //FindLatestByAccount
    @Query(nativeQuery = true, value = "SELECT * FROM transactions t WHERE t.account_id = :accountId ORDER BY t.dated DESC LIMIT :limit")
    List<Transaction> findLatestByAccount(Long accountId, Long limit);

    //FindByAccountAndTransactionId
    @Query("SELECT t FROM Transaction t WHERE (t.accountId = ?1 OR t.accountIdDestination = ?1) AND t.id = ?2")
    Transaction findByAccountAndId(Long accountId, Long transactionId);

    @Procedure("PRC_GET_FILTERED_TRANSACTIONS")
    List<Transaction> getFilteredTransactions(Long accountId, String type, String flow, Double min_amount, Double max_amount, Date min_date, Date max_date, Integer limit);

    @Query(nativeQuery = true,
    value = "SELECT acc_trans_aux.name, acc_trans_aux.cvu, MAX(acc_trans_aux.dated) AS last_interaction_date\n" +
            "FROM (SELECT IF(t.account_id = ?1, t.name_destination, t.name_origin) AS name,\n" +
            "\t\t\t IF(t.account_id = ?1, t.destination, t.origin) AS cvu,\n" +
            "\t\t\t dated\n" +
            "\t  FROM transactions t\n" +
            "\t  WHERE (t.account_id = ?1 OR t.account_id_destination = ?1) AND\n" +
            "\t  t.type = 'Transfer') AS acc_trans_aux\n" +
            "GROUP BY acc_trans_aux.name, acc_trans_aux.cvu\n" +
            "ORDER BY MAX(acc_trans_aux.dated) DESC\n" +
            "LIMIT 5;")
    List<Tuple> getRelatedAccounts(Long accountId);

}
