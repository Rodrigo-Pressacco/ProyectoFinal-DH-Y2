package com.dhmoney.userservice.controller;

import com.dhmoney.userservice.domain.model.dto.UserAccountDTO;
import com.dhmoney.userservice.domain.model.dto.UserRegistrationDTO;
import com.dhmoney.userservice.exception.KeycloakErrorException;
import com.dhmoney.userservice.exception.ResourceBadRequestException;
import com.dhmoney.userservice.exception.ResourceConflictException;
import com.dhmoney.userservice.exception.ResourceNotFoundException;
import com.dhmoney.userservice.service.IUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class UserControllerTest {

    @Mock
    IUserService service;

    @InjectMocks
    UserController controller;

    UserRegistrationDTO user;
    UserAccountDTO userAccountDTO;

    @BeforeAll
    public static void init(){
        //System.out.println("Before All init() method called");
    }

    @BeforeEach
    public void setUp() {
        user = UserRegistrationDTO.builder().build();
        userAccountDTO = UserAccountDTO.builder().build();
    }

    /*@Test
    void saveUserOk() throws KeycloakErrorException, ResourceBadRequestException, ResourceConflictException {
        given(service.saveUser(user)).willReturn(userAccountDTO);

        ResponseEntity<UserAccountDTO> response = controller.saveUser(user);
        assertEquals(response.getBody(), userAccountDTO);
    }*/

    @Test
    void getUsersOk() {
        List<UserAccountDTO> userAccountDTOList = List.of(userAccountDTO);
        given(service.getAllUsers()).willReturn(userAccountDTOList);

        ResponseEntity<List<UserAccountDTO>> response = controller.getUsers();
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), userAccountDTO);
    }

    @Test
    void deleteByIdOk() throws ResourceNotFoundException, KeycloakErrorException {
        ResponseEntity<?> response = controller.deleteById(1L);
        assertEquals(Objects.requireNonNull(response.getBody()), "User Deleted...");
    }

    @Test
    void getByIdOk() throws ResourceNotFoundException {
        given(service.getUser(1L)).willReturn(userAccountDTO);
        UserAccountDTO response = controller.getById(1L);
        assertEquals(response, userAccountDTO);
    }
}
