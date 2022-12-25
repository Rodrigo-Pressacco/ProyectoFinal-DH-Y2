package com.dhmoney.userservice.controller;

import com.dhmoney.userservice.domain.model.ErrorResponse;
import com.dhmoney.userservice.domain.model.dto.TokenResponse;
import com.dhmoney.userservice.domain.model.dto.UserLoginDTO;
import com.dhmoney.userservice.domain.model.dto.UserRegistrationDTO;
import com.dhmoney.userservice.exception.*;
import com.dhmoney.userservice.service.impl.UserService;
import com.dhmoney.userservice.utils.Constants;
import com.dhmoney.userservice.utils.JsonResponseMessage;
import com.dhmoney.userservice.utils.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.LogoutToken;
import org.keycloak.representations.adapters.action.LogoutAction;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER, description = "Authorization")
public class AuthenticationController {

    @Autowired
    private Keycloak keycloakClient;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Log a user into Keycloak")
    @ApiResponse(responseCode = "200", description = "User logged in", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))})
    @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =
            ErrorResponse.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "User forbidden", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = com.nimbusds.oauth2.sdk.ErrorResponse.class))})
    public ResponseEntity<?> login(@RequestBody UserLoginDTO login) throws ResourceBadRequestException,
            ResourceUnauthorizedException, ResourceNotFoundException, ForbiddenException {

        if (login.getEmail() == null) {
            throw new ResourceBadRequestException("The user's email is required");
        }
        if (!validator.validateEmail(login.getEmail())) {
            throw new ResourceBadRequestException("The user's email is invalid");
        }
        if (login.getPassword() == null) {
            throw new ResourceBadRequestException("The user's password is required");
        }
        if (!validator.validatePassword(login.getPassword())) {
            throw new ResourceBadRequestException("The password must be between 6 and 20 characters, at least one digit, at least one lowercase, and at least one uppercase.");
        }
        // Esto es para que tire exception si el email no existe
        userService.getUserByEmail(login.getEmail());

        TokenResponse tokenReponse = userService.login(login);

        return ResponseEntity.ok(tokenReponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    @ApiResponse(responseCode = "200", description = "The user has logged out ", content = {@Content(mediaType = "application/json")})
    @ApiResponse(responseCode = "400", description = "Bad Request the user has no active sessions", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = com.nimbusds.oauth2.sdk.ErrorResponse.class))})
    public ResponseEntity<?> logout(HttpServletRequest request) throws ResourceBadRequestException, KeycloakErrorException {

        //System.out.println("Token = " + request.getHeader("Authorization").substring(7));

        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            String RSAKey = keycloakClient.realm(Constants.realm).keys().getKeyMetadata().getKeys().stream().filter(k -> k.getAlgorithm().equals("RS256")).findFirst().get().getPublicKey();
            X509EncodedKeySpec pubKeySpecX509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(RSAKey));
            publicKey = kf.generatePublic(pubKeySpecX509EncodedKeySpec);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new KeycloakErrorException("Internal Server Error");
        }

        Claims claims = null;
        try {
             claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(request.getHeader("Authorization").substring(7))
                    .getBody();
        }
        catch (JwtException ex) {
            log.warn(ex.getMessage());
            throw new ResourceBadRequestException("Invalid token");
        }

        try {
            String keycloakUserId = claims.getSubject();
            UserResource userResource = keycloakClient.realm(Constants.realm).users().get(keycloakUserId);

            if (keycloakClient.realm(Constants.realm).users().get(keycloakUserId).getUserSessions().isEmpty())
                throw new ResourceBadRequestException("The user " + userResource.toRepresentation().getUsername() + " has no active sessions.");

            keycloakClient.realm(Constants.realm).users().get(keycloakUserId).logout();
            return ResponseEntity.ok(new JsonResponseMessage("The user " + userResource.toRepresentation().getUsername() + " has logged out."));

        } catch (JwtException ex) {
            throw new ResourceBadRequestException(ex.getMessage());
        }

    }

    @PostMapping("/reset-password")
    @Operation(summary = "User reset password")
    @ApiResponse(responseCode = "200", description = "Ok", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserLoginDTO.class))})
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = com.nimbusds.oauth2.sdk.ErrorResponse.class))})
    public ResponseEntity<?> resetPassword(@RequestBody UserLoginDTO login) throws ResourceNotFoundException {
        // Esto es para que tire exception si el email no existe
        userService.getUserByEmail(login.getEmail());

        UserRepresentation userRepresentation = keycloakClient.realm(Constants.realm).users().search(login.getEmail()).get(0);
        UserResource userResource = keycloakClient.realm(Constants.realm).users().get(userRepresentation.getId());
        new Thread( () -> userResource.resetPasswordEmail() ).start();
        return ResponseEntity.ok(new JsonResponseMessage("Email for resetting password is being sent..."));
    }

    @GetMapping("/validate-token")
    @Operation(summary = "User token validations")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Ok validated token", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = com.nimbusds.oauth2.sdk.ErrorResponse.class))})
    public ResponseEntity<?> validateToken(HttpServletRequest request) throws KeycloakErrorException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet requestUserInfo = new HttpGet(Constants.keycloakUserInfoURL);
        requestUserInfo.addHeader("Authorization", request.getHeader("Authorization"));

        try {
            CloseableHttpResponse response = httpClient.execute(requestUserInfo);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String stringResponse = EntityUtils.toString(entity);
                JSONObject jsonResponse = new JSONObject(stringResponse);
                if (jsonResponse.has("error_description"))
                    return ResponseEntity.ok(new JsonResponseMessage("The provided token is INVALID"));
                else
                    return ResponseEntity.ok(new JsonResponseMessage("The provided token is VALID"));
            }
            throw new KeycloakErrorException("Error while validating token");
        } catch (Exception e) {
            throw new KeycloakErrorException("Error while validating token");
        }

    }

}
