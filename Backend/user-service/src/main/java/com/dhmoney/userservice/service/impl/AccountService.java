package com.dhmoney.userservice.service.impl;

import com.dhmoney.userservice.domain.model.dto.AccountDTO;
import com.dhmoney.userservice.domain.model.dto.AccountFullDTO;
import com.dhmoney.userservice.domain.model.dto.AccountNameDTO;
import com.dhmoney.userservice.domain.model.dto.AliasDTO;
import com.dhmoney.userservice.domain.repository.AccountFeignRepository;
import com.dhmoney.userservice.exception.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    @Autowired
    private final AccountFeignRepository repository;

    @CircuitBreaker(name = "circuitbreaker_config", fallbackMethod = "getByUserFallback")
    @Retry(name = "retry_config")
    public AccountDTO getByUser(Long user) throws ResourceNotFoundException {
        log.info("Retrieving account for user with ID " + user);
        return repository.getByUser(user);
    }

    @CircuitBreaker(name = "circuitbreaker_config")
    @Retry(name = "retry_config")
    public AccountFullDTO getFullAccountByUser(Long user) throws ResourceNotFoundException {
        log.info("Retrieving full account for user with ID " + user);
        return repository.getFullAccountByUser(user);
    }

    @CircuitBreaker(name = "circuitbreaker_config", fallbackMethod = "createAccountFallback")
    public AccountDTO createAccount(Long user, String name) {
        AccountNameDTO accountNameDTO = new AccountNameDTO();
        accountNameDTO.setName(name);
        log.info("Creating account for user with ID " + user);
        return repository.createAccount(user, accountNameDTO);
    }

    /*public AccountDTO patchAccountAlias(Long user, AliasDTO aliasDTO)
            throws ResourceBadRequestException {
        log.info("Updating account alias for user with ID " + user);
        return repository.patchAccount(user, aliasDTO);
    }*/

    // Sin CircuitBreaker porque queremos que la excepción proveniente de Feign siga su curso
    public AccountDTO updateUserName(Long user, AccountNameDTO accountNameDTO) {
        return repository.updateUserName(user, accountNameDTO);
    }


    // Fallbacks

    private AccountDTO getByUserFallback(Long user, CallNotPermittedException exception) {
        return null;
    }

    private AccountDTO createAccountFallback(Long user, String name, CallNotPermittedException exception) {
        return null;
    }

}
