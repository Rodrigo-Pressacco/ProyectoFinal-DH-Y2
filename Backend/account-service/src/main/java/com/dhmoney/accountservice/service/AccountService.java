package com.dhmoney.accountservice.service;

import com.dhmoney.accountservice.client.UserClient;
import com.dhmoney.accountservice.domain.model.Account;
import com.dhmoney.accountservice.domain.model.dto.AccountDTO;
import com.dhmoney.accountservice.domain.model.dto.AccountNameDTO;
import com.dhmoney.accountservice.domain.repository.AccountRepository;
import com.dhmoney.accountservice.exception.ResourceBadRequestException;
import com.dhmoney.accountservice.exception.ResourceConflictException;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.dhmoney.accountservice.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Slf4j
@Service("AccountService")
public class AccountService {

    AccountRepository repository;
    UserClient userClient;
    ObjectMapper mapper;
    ModelMapper modelMapper;
    Validator validator;

    @Autowired
    public AccountService(AccountRepository repository, UserClient userClient
            , ObjectMapper mapper, ModelMapper modelMapper, Validator validator) {
        this.repository = repository;
        this.userClient = userClient;
        this.mapper = mapper;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    public AccountDTO save(Account bankAccount) {
        log.info("Saving account to local DB");
        Account bank = repository.save(bankAccount);
        return mapper.convertValue(bank,
                AccountDTO.class);
    }

    public AccountDTO findById(Long id) {
        log.info("Retrieving account with ID " + id);
        return mapper.convertValue(repository.findById(id), AccountDTO.class);
    }

    public AccountDTO findByCVU(String cvu) throws ResourceNotFoundException {
        log.info("Retrieving account with CVU " + cvu);
        Account account = repository.findByCVU(cvu);
        if (account == null) {
            throw new ResourceNotFoundException("Account with CVU " + cvu + " doesn't exist");
        }
        return mapper.convertValue(account, AccountDTO.class);
    }

    public List<AccountDTO> findAll() {
        log.info("Retrieving all accounts");
        Collection<Account> users = repository.findAll();
        return modelMapper.map(users,
                new TypeToken<List<AccountDTO>>() {}.getType());
    }

    public Boolean delete(Long id) {
        log.info("Deleting account with ID " + id);
        if (findById(id) != null) {
            repository.deleteById(id);
            log.info("Account with ID " + id + " was deleted");
            return true;
        }
        return false;
    }

    public AccountDTO create() {
        AccountDTO bankAccountDTO = new AccountDTO();

        log.info("Creating CVU for new account");
        String cvu = null;
        Account bankCVUSearched = null;
        do {
            cvu = createCVU();
            //Analyze if this cvu exists in DB
            bankCVUSearched = repository.findByCVU(cvu);
        } while(bankCVUSearched != null);

        bankAccountDTO.setCvu(cvu);

        log.info("Creating alias for new account");
        String alias = null;
        Account bankAliasSearched = null;

        do {
            alias = createAlias();
            //Analyze if this alias exists in DB
            log.info("Retrieving account with alias " + alias);
            bankAliasSearched = repository.findByAlias(alias);
        } while(bankAliasSearched != null);

        bankAccountDTO.setAlias(alias);

        return bankAccountDTO;
    }

    public String createAlias(){
        String alias = null;
        try {
            File file = new File("alias.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            List list = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }

            List threeWords = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                int rand = new Random().nextInt(list.size());
                threeWords.add(list.get(rand));
            }

            alias =
                    threeWords.get(0) + "." + threeWords.get(1) + "." + threeWords.get(2);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return alias;
    }

    public String createCVU() {
        long base = 1000000000;
        double first10digits = base + Math.random() * (9 * Math.pow(10,
                9));
        double second12digits = base*100 + Math.random() * (9 * Math.pow(10,
                11));
        long longFirst = (long) first10digits;
        long longSecond = (long) second12digits;
        return String.valueOf(longFirst) + String.valueOf(longSecond);
    }

    public AccountDTO getAccountByUser(Long user) throws ResourceNotFoundException {
        log.info("Retrieving account for user with ID " + user);
        Account account = repository.findByUser(user);
        if (account == null) {
            throw new ResourceNotFoundException("User with ID " + user + " doesn't exist");
        }
        return mapper.convertValue(account, AccountDTO.class);
    }

    public AccountDTO createAccount(Long user, String name) {
        AccountDTO accountDTO = create();
        Account account = mapper.convertValue(accountDTO, Account.class);
        account.setUserId(user);
        account.setBalance(0.0);
        account.setName(name);

        AccountDTO accoutnDTOSaved = save(account);
        log.info("An account with ID " + accoutnDTOSaved.getId() + " was created.");

        return accoutnDTOSaved;
    }

    public AccountDTO patchAccount(Long user, String alias) throws ResourceConflictException, ResourceBadRequestException {
        log.info("Retrieving account with alias " + alias);
        Account bankAliasSearched = repository.findByAlias(alias);

        if (bankAliasSearched != null) {
            if (bankAliasSearched.getUserId() != user){
                throw new ResourceConflictException("The alias is already in use by another account");
            }
        }

        log.info("Validating new alias");
        if (!validator.validateAlias(alias)) {
            throw new ResourceBadRequestException("The alias is invalid");
        }

        log.info("Retrieving account for user with ID " + user);
        Account originalAccount = repository.findByUser(user);
        originalAccount.setAlias(alias);

        return save(originalAccount);
    }

    /*public String getUserIamIDByID(Long id) throws ResourceNotFoundException {
        log.info("Retrieving Keycloak ID for user with ID " + id);
        return userClient.getIamIDByID(id).getBody();
    }*/

    public void setBalance(String cvu, Double newBalance) throws ResourceNotFoundException {
        log.info("Retrieving account with CVU " + cvu);
        Account account = repository.findByCVU(cvu);

        if (account == null) {
            throw new ResourceNotFoundException("Account with CVU " + cvu + " doesn't exist");
        }

        account.setBalance(newBalance);
        save(account);
    }

    public AccountDTO updateUserName(Long user, AccountNameDTO accountNameDTO) {
        log.info("Retrieving account for user with ID " + user);
        Account account = repository.findByUser(user);
        account.setName(accountNameDTO.getName());

        log.info("Updated name for account with ID " + user);
        return save(account);
    }
}
