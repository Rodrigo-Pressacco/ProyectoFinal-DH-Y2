package com.dhmoney.userservice.service;

import com.dhmoney.userservice.domain.model.User;
import com.dhmoney.userservice.domain.model.dto.UserAccountDTO;
import com.dhmoney.userservice.domain.model.dto.UserRegistrationDTO;
import com.dhmoney.userservice.domain.repository.KeycloakRepository;
import com.dhmoney.userservice.domain.repository.UserRepository;
import com.dhmoney.userservice.exception.ResourceBadRequestException;
import com.dhmoney.userservice.service.impl.UserService;
import com.dhmoney.userservice.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class UserServiceTest {
//    @Mock
//    KeycloakRepository keycloakRepository;
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    BankAccountService bankAccountService;
//
//    @Mock
//    ObjectMapper objectMapper;
//
//    @Mock
//    ModelMapper modelMapper;
//
//    @Mock
//    Validator validator;
//
//    @InjectMocks
//    UserService service;
//
//    User user;
//    UserAccountDTO userAccountDTO;
//    BankAccount bankAccount;
//    BankAccountDTO bankAccountDTO;
//    UserRegistrationDTO userRegistrationDTO;
//
//    @BeforeAll
//    public static void init(){
//        System.out.println("Before All init() method called");
//    }
//
//    @BeforeEach
//    public void setUp() {
//        user = User.builder()
//                .id(1L)
//                .email("xxx@d.com").build();
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
//        List<BankAccountDTO> bankAccountList = List.of(bankAccountDTO);
//        userAccountDTO = UserAccountDTO.builder()
//                .bankAccounts(bankAccountList)
//                .id(1L)
//                .email("xxx@d.com").build();
//        userRegistrationDTO = UserRegistrationDTO.builder().build();
//    }
//
//    @DisplayName("JUnit test for getAllUsers method")
//    @Test
//    void getAllUsersOk(){
//        List<User> users = List.of(user);
//        List<UserAccountDTO> usersAccountList = List.of(userAccountDTO);
//        given(userRepository.findAll()).willReturn(users);
//        given(modelMapper.map(users, new TypeToken<List<UserAccountDTO>>() {}.getType())).willReturn((usersAccountList));
//
//        List<UserAccountDTO> getResponse = service.getAllUsers();
//        assertEquals(userAccountDTO, getResponse.get(0));
//    }
//
//    /*@DisplayName("JUnit test for saveUsers method")
//    @Test
//    void saveUsersOk() throws ResourceBadRequestException {
//        given(userRepository.findAll()).willReturn(users);
//        given(modelMapper.map(users, new TypeToken<List<UserAccountDTO>>() {}.getType())).willReturn((usersAccountList));
//
//        List<UserAccountDTO> getResponse = service.getAllUsers();
//        assertEquals(userAccountDTO, getResponse.get(0));
//    }*/
//
//    @ParameterizedTest
//    @MethodSource("isContainRuleData")
//    void isContainRule(UserRegistrationDTO userRegistrationDTO, String email, String firstname, String lastname, String dni, String phone, String password){
//        userRegistrationDTO.setEmail(email);
//        userRegistrationDTO.setFirstname(firstname);
//        userRegistrationDTO.setLastname(lastname);
//        userRegistrationDTO.setDni(dni);
//        userRegistrationDTO.setPhone(phone);
//        userRegistrationDTO.setPassword(password);
//
//        given(validator.validateEmail(email)).willReturn(true);
//        given(validator.validateNames(firstname)).willReturn(true);
//        given(validator.validateNames(lastname)).willReturn(true);
//        given(validator.validatePhone(phone)).willReturn(true);
//        given(validator.validatePassword(password)).willReturn(false);
//        assertThrows(ResourceBadRequestException.class, ()-> service.validateUser(userRegistrationDTO));
//    }
//
//    private static Stream<Arguments> isContainRuleData(){
//
//        UserRegistrationDTO build = UserRegistrationDTO.builder().build();
//
//        return Stream.of(
//                Arguments.of(build,null,null,null,null,null,null),
//                Arguments.of(build,"hola",null,null,null,null,null),
//                Arguments.of(build,"hola@gmail.com",null,null,null,null,null),
//                Arguments.of(build,"hola@gmail.com","juan",null,null,null,null),
//                Arguments.of(build,"hola@gmail.com","juan","pulido",null,null,null),
//                Arguments.of(build,"hola@gmail.com","juan","pulido","123",null,null),
//                Arguments.of(build,"hola@gmail.com","juan","pulido","123","321",null),
//                Arguments.of(build,"hola@gmail.com","juan","pulido","123","321","5203875")
//        );
//    }
}
