package com.dhmoney.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BankAccountServiceTest {
//    @Mock
//    BankAccountRepository repository;
//
//    @Mock
//    ModelMapper modelMapper;
//
//    @Mock
//    ObjectMapper objectMapper;
//
//    @InjectMocks
//    BankAccountService bankAccountService;
//
//    User user;
//    BankAccount bankAccount;
//    BankAccountDTO bankAccountDTO;
//
//    @BeforeAll
//    public static void init(){
//        System.out.println("Before All init() method called");
//    }
//
//    @BeforeEach
//    public void setUp() {
//        user = User.builder().build();
//        bankAccount = BankAccount.builder()
//                .cvu("123")
//                .alias("a.b.c")
//                .id(1L)
//                .user(user)
//                .build();
//        bankAccountDTO = BankAccountDTO.builder()
//                .alias("a.b.c")
//                .cvu("123")
//                .build();
//    }
//
//    @DisplayName("JUnit test for save method")
//    @Test
//    void saveOk(){
//        given(repository.save(bankAccount)).willReturn(bankAccount);
//        when(objectMapper.convertValue(any(), (Class<Object>) any())).thenReturn(bankAccountDTO);
//
//        BankAccountDTO saveResponse = bankAccountService.save(bankAccount);
//        assertEquals(bankAccountDTO, saveResponse);
//    }
//
//    @DisplayName("JUnit test for findById method")
//    @Test
//    void findByIdOk(){
//        given(repository.findById(1L)).willReturn(Optional.empty());
//        when(objectMapper.convertValue(any(), (Class<Object>) any())).thenReturn(bankAccountDTO);
//
//        BankAccountDTO byId = bankAccountService.findById(1L);
//        assertEquals(bankAccountDTO, byId);
//    }
//
//    @DisplayName("JUnit test for findAll method")
//    @Test
//    void findAllOk(){
//        List<BankAccount> users = List.of(bankAccount);
//        List<BankAccountDTO> usersDTO = List.of(bankAccountDTO);
//        given(repository.findAll()).willReturn((users));
//        given(modelMapper.map(users, new TypeToken<List<BankAccountDTO>>() {}.getType())).willReturn((usersDTO));
//
//        List<BankAccountDTO> saveResponse = bankAccountService.findAll();
//        assertEquals(usersDTO, saveResponse);
//    }
//
//    @DisplayName("JUnit test for delete method")
//    @Test
//    void deleteOk(){
//        given(repository.findById(1L)).willReturn(Optional.empty());
//        when(objectMapper.convertValue(any(), (Class<Object>) any())).thenReturn(bankAccountDTO);
//        Boolean deleteResponse = bankAccountService.delete(1L);
//        assertTrue(deleteResponse);
//    }
//
//    @DisplayName("JUnit test for delete method false")
//    @Test
//    void deleteFalse(){
//        given(repository.findById(1L)).willReturn(Optional.empty());
//        Boolean deleteResponse = bankAccountService.delete(1L);
//        assertFalse(deleteResponse);
//    }
//
//    @DisplayName("JUnit test for update method false")
//    @Test
//    void updateOk(){
//        BankAccountDTO bankAccountDTO = bankAccountService.update(bankAccount);
//        assertNull(bankAccountDTO);
//    }
//
//    /*@DisplayName("JUnit test for createAlias method false")
//    @Test
//    void createAliasOk(){
//        String alias = bankAccountService.createAlias();
//        assertNotNull(alias);
//    }*/
//
//    @DisplayName("JUnit test for createCVU method false")
//    @Test
//    void createCVUOk(){
//        String cvu = bankAccountService.createCVU();
//        assertNotNull(cvu);
//    }
}
