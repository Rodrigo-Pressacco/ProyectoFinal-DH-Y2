package com.dhmoney.accountservice.client;

import com.dhmoney.accountservice.config.feign.FeignClientConfiguration;
import com.dhmoney.accountservice.domain.model.Transaction;
import com.dhmoney.accountservice.domain.model.dto.RelatedAccountDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionPublicDTO;
import com.dhmoney.accountservice.exception.ResourceEmptyField;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient(name = "dm-transaction", url = "http://localhost:8086", configuration = FeignClientConfiguration.class)
public interface TransactionClient {

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionPublicDTO>> getTransactions(
            @RequestParam(name = "account", required = false) Long account_id,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "flow", required = false) String flow,
            @RequestParam(name = "monto_min", required = false) Double min_amount,
            @RequestParam(name = "monto_max", required = false) Double max_amount,
            @RequestParam(name = "fecha_min", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date min_date,
            @RequestParam(name = "fecha_max", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date max_date,
            @RequestParam(name = "limit", required = false) Integer limit
    );

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> saveTransaction(@RequestBody Transaction transaction) throws ResourceNotFoundException, ResourceEmptyField;

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionPublicDTO> getById(@PathVariable Long id, @RequestParam("account") Long account_id) throws ResourceNotFoundException;

    @GetMapping("/transactions/related_accounts")
    public ResponseEntity<List<RelatedAccountDTO>> getRelatedAccounts(@RequestParam(name = "account", required = true) Long account_id);

}
