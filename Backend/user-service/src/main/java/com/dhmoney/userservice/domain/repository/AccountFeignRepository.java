package com.dhmoney.userservice.domain.repository;

import com.dhmoney.userservice.domain.model.dto.AccountDTO;
import com.dhmoney.userservice.domain.model.dto.AccountFullDTO;
import com.dhmoney.userservice.domain.model.dto.AccountNameDTO;
import com.dhmoney.userservice.domain.model.dto.AliasDTO;
import com.dhmoney.userservice.exception.ForbiddenException;
import com.dhmoney.userservice.exception.ResourceBadRequestException;
import com.dhmoney.userservice.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Repository
@FeignClient(name = "dm-account", url = "http://localhost:8082")
public interface AccountFeignRepository {

    @GetMapping("/accounts/{user}")
    AccountDTO getByUser(@PathVariable Long user) throws ResourceNotFoundException;

    @GetMapping("/accounts/{user}")
    AccountFullDTO getFullAccountByUser(@PathVariable Long user) throws ResourceNotFoundException;

    @PostMapping("/accounts/{user}")
    AccountDTO createAccount(@PathVariable Long user,
                             @RequestBody AccountNameDTO accountName);

    @PatchMapping("/accounts/name/{user}")
    AccountDTO updateUserName(@PathVariable Long user,
                              @RequestBody AccountNameDTO accountNameDTO);

    /*@PatchMapping("/accounts/{user}")
    AccountDTO patchAccount(@PathVariable Long user,
                            @RequestBody AliasDTO aliasDTO) throws ResourceBadRequestException;*/

}
