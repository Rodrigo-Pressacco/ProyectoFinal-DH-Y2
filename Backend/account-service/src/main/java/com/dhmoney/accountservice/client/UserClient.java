package com.dhmoney.accountservice.client;

import com.dhmoney.accountservice.config.feign.FeignClientConfiguration;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionDTO;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "dm-user", url = "http://localhost:8084", configuration = FeignClientConfiguration.class)
public interface UserClient {

    /*@GetMapping("/users/{id}/iam")
    public ResponseEntity<String> getIamIDByID(@PathVariable Long id) throws ResourceNotFoundException;*/
}
