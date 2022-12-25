package com.dhmoney.userservice.domain.repository;

import com.dhmoney.userservice.domain.model.UserKeycloak;
import com.dhmoney.userservice.exception.ForbiddenException;
import com.dhmoney.userservice.exception.KeycloakErrorException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakRepository{

    public Keycloak keycloakClient;

    @Value("${dh.keycloak.realm}")
    private String realm;

    @Autowired
    public KeycloakRepository(Keycloak keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    private UserKeycloak toUserKeycloak(UserRepresentation userRepresentation ) {
        return new UserKeycloak(userRepresentation.getId(), userRepresentation.getUsername(), null, userRepresentation.getEmail());
    }

    public List<UserKeycloak> findByUserName(String name) {
        List<UserRepresentation> users = keycloakClient.realm(realm).users().search(name);
        return users.stream().map(this::toUserKeycloak).collect(Collectors.toList());
    }

    public UserKeycloak findById(String id) {
        UserRepresentation userRepresentation = keycloakClient.realm(realm).users().get(id).toRepresentation();
        return toUserKeycloak(userRepresentation);
    }

    public UserKeycloak save(UserKeycloak userKeycloak) throws KeycloakErrorException {
        log.info("Saving user in Keycloak");
        CredentialRepresentation credencial = new CredentialRepresentation();
        credencial.setType(CredentialRepresentation.PASSWORD);
        credencial.setValue(userKeycloak.getPassword());
        credencial.setTemporary(false);

        UserRepresentation userRepresentationToSave = new UserRepresentation();
        userRepresentationToSave.setEmail(userKeycloak.getEmail());
        userRepresentationToSave.setUsername(userKeycloak.getUsername());
        userRepresentationToSave.setCredentials(Collections.singletonList(credencial));

        //System.out.println("UserRepresentation username. " + userRepresentationToSave.getUsername());
        //System.out.println("UserRepresentation credentials. " + userRepresentationToSave.getCredentials().toString());

        Response response =
                keycloakClient.realm(realm).users().create(userRepresentationToSave);
        log.info("Response from Keycloak: " + response.toString());
        log.info("Response status: " + response.getStatus());
        if (response.getStatus() != 201) {
            throw new KeycloakErrorException("Error while saving user");
        }
        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info("Keycloak created ID: " + userId);

        UserRepresentation userRepresentationSaved = keycloakClient.realm(realm).users().search(userRepresentationToSave.getUsername()).get(0);
        userRepresentationSaved.setEnabled(true);
        //userRepresentationSaved.setEmailVerified(true);
        keycloakClient.realm(realm).users().get(userRepresentationSaved.getId()).update(userRepresentationSaved);

        UserResource userResource = keycloakClient.realm(realm).users().get(userId);
        new Thread( () -> userResource.sendVerifyEmail() ).start();

        UserKeycloak userKeycloakSaved = UserKeycloak.builder()
                .id(userRepresentationSaved.getId())
                .username(userRepresentationSaved.getUsername())
                .email(userRepresentationSaved.getEmail())
                .build();

        return userKeycloakSaved;
    }

    public void setUserIDAttribute(UserKeycloak userKeycloak, Long id) {
        UserRepresentation userRepresentation = keycloakClient.realm(realm).users().search(userKeycloak.getUsername()).get(0);

        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("user_id", Collections.singletonList(id.toString()));
        userRepresentation.setAttributes(map);

        keycloakClient.realm(realm).users().get(userKeycloak.getId()).update(userRepresentation);
    }

    public UserKeycloak update(UserKeycloak userKeycloak) {
        UserRepresentation userRepresentationToUpdate = new UserRepresentation();
        userRepresentationToUpdate.setId(userKeycloak.getId());
        userRepresentationToUpdate.setUsername(userKeycloak.getUsername());
        userRepresentationToUpdate.setEmail(userKeycloak.getEmail());
        if (userKeycloak.getPassword() != null) {
            CredentialRepresentation credencial = new CredentialRepresentation();
            credencial.setType(CredentialRepresentation.PASSWORD);
            credencial.setValue(userKeycloak.getPassword());
            credencial.setTemporary(false);
            userRepresentationToUpdate.setCredentials(Collections.singletonList(credencial));
        }

        keycloakClient.realm(realm).users().get(userRepresentationToUpdate.getId()).update(userRepresentationToUpdate);
        UserRepresentation userRepresentationUpdated = keycloakClient.realm(realm).users().get(userRepresentationToUpdate.getId()).toRepresentation();

        UserKeycloak userKeycloakUpdated = UserKeycloak.builder()
                .id(userRepresentationUpdated.getId())
                .username(userRepresentationUpdated.getUsername())
                .email(userRepresentationUpdated.getEmail())
                .build();

        return userKeycloakUpdated;
    }

    public void delete(String id) throws KeycloakErrorException {
        Response response = keycloakClient.realm(realm).users().delete(id);
        if (response.getStatus() != 204) {
            throw new KeycloakErrorException("Error while deleting user");
        }
    }

    public void checkIfEmailIsVerified(String email) throws ForbiddenException {
        UserRepresentation userRepresentation = keycloakClient.realm(realm).users().search(email).get(0);
        Boolean isEmailVerified = userRepresentation.isEmailVerified();
        if (!isEmailVerified) {
            throw new ForbiddenException("The user's email has not been verified");
        }
    }

}

