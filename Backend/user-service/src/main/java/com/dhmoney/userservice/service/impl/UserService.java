package com.dhmoney.userservice.service.impl;

import com.dhmoney.userservice.domain.model.User;
import com.dhmoney.userservice.domain.model.dto.*;
import com.dhmoney.userservice.domain.model.UserKeycloak;
import com.dhmoney.userservice.domain.repository.KeycloakRepository;
import com.dhmoney.userservice.domain.repository.UserRepository;
import com.dhmoney.userservice.exception.*;
import com.dhmoney.userservice.service.IUserService;
import com.dhmoney.userservice.utils.Constants;
import com.dhmoney.userservice.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {

    private final KeycloakRepository keycloakRepository;
    private final UserRepository repository;
    private final AccountService accountService;
    private final Validator validator;
    ObjectMapper mapper;
    ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository repository,
                       ObjectMapper mapper,
                       ModelMapper modelMapper,
                       KeycloakRepository keycloakRepository, AccountService accountService, Validator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.modelMapper = modelMapper;
        this.keycloakRepository = keycloakRepository;
        this.accountService = accountService;
        this.validator = validator;
    }

    @Override
    public List<UserAccountDTO> getAllUsers() {
        List<User> listUsers = repository.findAll();
        return modelMapper.map(listUsers,
                new TypeToken<List<UserAccountDTO>>(){}.getType());
    }

    @Override
    public UserAccountDTO saveUser(UserRegistrationDTO userRegistrationDTO)
            throws ResourceBadRequestException, ResourceConflictException, KeycloakErrorException {

        log.info("Validating user data");
        validateUser(userRegistrationDTO);

        Optional<User> userSameEmail = repository.findByEmail(userRegistrationDTO.getEmail());
        if (userSameEmail.isPresent()) {
            throw new ResourceConflictException("A user with the same email already exists");
        }

        log.info("Saving user to Keycloak DB");
        UserKeycloak userKeycloak = UserKeycloak.builder()
                .username(userRegistrationDTO.getEmail())
                .password(userRegistrationDTO.getPassword())
                .email(userRegistrationDTO.getEmail())
                .build();
        UserKeycloak userKeycloakSaved = keycloakRepository.save(userKeycloak);
            

        log.info("Saving user to local DB");
        User user = User.builder()
                .iamId(userKeycloakSaved.getId())
                .email(userRegistrationDTO.getEmail())
                .firstName(userRegistrationDTO.getFirstName())
                .lastName(userRegistrationDTO.getLastName())
                .dni(userRegistrationDTO.getDni().toString())
                .phone(userRegistrationDTO.getPhone())
                .build();
        User userSaved = repository.save(user);

        keycloakRepository.setUserIDAttribute(userKeycloakSaved, userSaved.getId());

        log.info("Creating account for user with ID " + userSaved.getId());
        String name = userSaved.getFirstName() + " " + userSaved.getLastName();
        AccountDTO account = accountService.createAccount(userSaved.getId(), name);

        UserAccountDTO userAccountDTO = modelMapper.map(userSaved, UserAccountDTO.class);
        userAccountDTO.setAccount(account);

        return userAccountDTO;
    }

    @Override
    public void deleteUser(Long id) throws ResourceNotFoundException, KeycloakErrorException {
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + id + " doesn't exist");
        }

        log.info("Deleting user with ID " + id);
        keycloakRepository.delete(user.getIamId());
        repository.deleteById(id);
        log.info("User with ID " + id + " was deleted");
    }

    @Override
    public UserAccountDTO getUser(Long id) throws ResourceNotFoundException {
        log.info("Retrieving user with ID " + id);
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + id + " doesn't exist");
        }
        UserAccountDTO userAccount = modelMapper.map(user, UserAccountDTO.class);

        log.info("Retrieving account for user with ID " + id);
        userAccount.setAccount(accountService.getByUser(id));

        return userAccount;
    }

    @Override
    public String getUserIamIDByID(Long id) throws ResourceNotFoundException {
        log.info("Retrieving user with ID " + id);
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + id + " doesn't exist");
        }
        return user.getIamId();
    }

    @Override
    public UserAccountDTO getUserByEmail(String email) throws ResourceNotFoundException {
        log.info("Retrieving user with email " + email);
        User user = repository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with email " + email + " doesn't exist");
        }
        UserAccountDTO userAccountDTO = modelMapper.map(user, UserAccountDTO.class);

        log.info("Retrieving account for user with ID " + user.getId());
        userAccountDTO.setAccount(accountService.getByUser(user.getId()));

        return userAccountDTO;
    }

    @Override
    public TokenResponse login(UserLoginDTO login) throws ResourceUnauthorizedException,
            ResourceBadRequestException, ForbiddenException {

        log.info("Logging in for user with email " + login.getEmail());
        keycloakRepository.checkIfEmailIsVerified(login.getEmail());

        TokenResponse tokenResponse = null;
        try {
            Keycloak keycloakClient = null;
            TokenManager tokenManager = null;
            keycloakClient = Keycloak.getInstance(
                    Constants.serverUrl, Constants.realm, login.getEmail(),
                    login.getPassword(), Constants.clientId, Constants.clientSecret);

            tokenManager = keycloakClient.tokenManager();

            tokenResponse = TokenResponse.builder()
                    .accessToken(tokenManager.getAccessTokenString())
                    .expires_in(tokenManager.getAccessToken().getExpiresIn())
                    .refresh_token(tokenManager.getAccessToken().getRefreshToken())
                    .build();
        } catch (Exception e) {
            log.debug(e.getMessage());
            if (e.getMessage().equalsIgnoreCase("HTTP 401 Unauthorized")){
                throw new ResourceUnauthorizedException("Unauthorized");
            }
            throw new ResourceBadRequestException("Bad request");
        }

        return tokenResponse;
    }

    @Override
    public UserDTO patchByID(Long id, UserRegistrationDTO userPatchDTO)
            throws ResourceConflictException, KeycloakErrorException, ResourceBadRequestException, ResourceNotFoundException {
        log.info("Retrieving user with ID " + id);
        User originalUser = repository.findById(id).orElse(null);

        if (originalUser == null) {
            throw new ResourceNotFoundException("User with ID " + id + " doesn't exist");
        }

        if (userPatchDTO.getEmail() == null) {
            userPatchDTO.setEmail(originalUser.getEmail());
        }
        if (userPatchDTO.getFirstName() == null) {
            userPatchDTO.setFirstName(originalUser.getFirstName());
        }
        if (userPatchDTO.getLastName() == null) {
            userPatchDTO.setLastName(originalUser.getLastName());
        }
        if (userPatchDTO.getDni() == null) {
            userPatchDTO.setDni(Long.parseLong(originalUser.getDni()));
        }
        if (userPatchDTO.getPhone() == null) {
            userPatchDTO.setPhone(originalUser.getPhone());
        }

        log.info("Validating user data");
        validateUserWithoutPassword(userPatchDTO);

        log.info("Retrieving user with email " + userPatchDTO.getEmail());
        User userSameEmail = repository.findByEmail(userPatchDTO.getEmail()).orElse(null);
        if (userSameEmail != null && !userSameEmail.getId().equals(id)) {
            throw new ResourceConflictException("The email " + userPatchDTO.getEmail() + " is already taken by another user");
        }

        //System.out.println("Update user name firstname " + (userPatchDTO.getFirstName().equals(originalUser.getFirstName())));
        //System.out.println("update user name lastname " + (userPatchDTO.getLastName().equals(originalUser.getLastName())));
        //System.out.println("user saved: " + userPatchDTO.getFirstName() + " " + userPatchDTO.getLastName());
        //System.out.println("originalUser: " + originalUser.getFirstName() + " " + originalUser.getLastName());

        // Update name in account
        System.out.println(userPatchDTO.getFirstName());
        System.out.println(originalUser.getFirstName());
        if (!(userPatchDTO.getFirstName().equals(originalUser.getFirstName())) || !(userPatchDTO.getLastName().equals(originalUser.getLastName()))) {
            log.info("Updating name for account with ID " + id);
            AccountNameDTO accountNameDTO = new AccountNameDTO();
            String name = userPatchDTO.getFirstName() + " " +  userPatchDTO.getLastName();
            accountNameDTO.setName(name);

            AccountDTO accountDTO = accountService.updateUserName(id, accountNameDTO);
            log.info("Updated name for account with ID " + id);
        }

        log.info("Updating user in Keycloak DB");
        UserKeycloak userKeycloak = UserKeycloak.builder()
                .id(originalUser.getIamId())
                .username(userPatchDTO.getEmail())
                .email(userPatchDTO.getEmail())
                .password(userPatchDTO.getPassword())
                .build();
        UserKeycloak userKeycloakSaved = keycloakRepository.update(userKeycloak);

        log.info("Updating user in local DB");
        User user = User.builder()
                .id(id)
                .iamId(originalUser.getIamId())
                .email(userPatchDTO.getEmail())
                .firstName(userPatchDTO.getFirstName())
                .lastName(userPatchDTO.getLastName())
                .dni(userPatchDTO.getDni().toString())
                .phone(userPatchDTO.getPhone())
                .build();
        User userSaved = repository.save(user);

        UserDTO userDTO = mapper.convertValue(userSaved, UserDTO.class);

        return userDTO;
    }

    public void validateUserWithoutPassword(UserRegistrationDTO userRegistrationDTO) throws ResourceBadRequestException {
        if (userRegistrationDTO.getEmail() == null) {
            throw new ResourceBadRequestException("The user's email is required");
        }
        if (userRegistrationDTO.getFirstName() == null) {
            throw new ResourceBadRequestException("The user's firstname is " +
                    "required");
        }
        if (userRegistrationDTO.getLastName() == null) {
            throw new ResourceBadRequestException("The user's lastname is " +
                    "required");
        }
        if (userRegistrationDTO.getDni() == null) {
            throw new ResourceBadRequestException("The user's DNI is required");
        }
        if (userRegistrationDTO.getPhone() == null) {
            throw new ResourceBadRequestException("The user's phone is " +
                    "required");
        }

        if (!validator.validateEmail(userRegistrationDTO.getEmail())) {
            throw new ResourceBadRequestException("The user's email is " +
                    "invalid");
        }
        if (!validator.validateNames(userRegistrationDTO.getFirstName())) {
            throw new ResourceBadRequestException("The user's firstname is " +
                    "invalid");
        }
        if (!validator.validateNames(userRegistrationDTO.getLastName())) {
            throw new ResourceBadRequestException("The user's lastname is " +
                    "invalid");
        }
        if (!validator.validatePhone(userRegistrationDTO.getPhone())){
            throw new ResourceBadRequestException("The user's phone is " +
                    "invalid");
        }
    }

    public void validateUser(UserRegistrationDTO userRegistrationDTO) throws ResourceBadRequestException {
        
        validateUserWithoutPassword(userRegistrationDTO);
        
        if (userRegistrationDTO.getPassword() == null) {
            throw new ResourceBadRequestException("The user's password is required");
        }
        
        if (!validator.validatePassword(userRegistrationDTO.getPassword())) {
            throw new ResourceBadRequestException("The password must be between 6 and 20 characters, at least one digit, at least one lowercase, and at least one uppercase.");
        }
    }

}
