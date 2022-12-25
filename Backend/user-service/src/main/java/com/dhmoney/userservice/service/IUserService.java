package com.dhmoney.userservice.service;

import com.dhmoney.userservice.domain.model.User;
import com.dhmoney.userservice.domain.model.dto.*;
import com.dhmoney.userservice.exception.*;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<UserAccountDTO> getAllUsers();

    UserAccountDTO saveUser(UserRegistrationDTO userRegistrationDTO) throws ResourceBadRequestException,
            ResourceConflictException, KeycloakErrorException;

    void deleteUser(Long id) throws ResourceNotFoundException, KeycloakErrorException;

    UserAccountDTO getUser(Long id) throws ResourceNotFoundException;

    UserAccountDTO getUserByEmail(String email) throws ResourceNotFoundException;

    TokenResponse login(UserLoginDTO login) throws ResourceUnauthorizedException,
            ResourceBadRequestException, ForbiddenException;

    UserDTO patchByID(Long id, UserRegistrationDTO userRegistrationDTO) throws ResourceConflictException,
            KeycloakErrorException, ResourceBadRequestException, ResourceNotFoundException;

    String getUserIamIDByID(Long id) throws ResourceNotFoundException;
}
