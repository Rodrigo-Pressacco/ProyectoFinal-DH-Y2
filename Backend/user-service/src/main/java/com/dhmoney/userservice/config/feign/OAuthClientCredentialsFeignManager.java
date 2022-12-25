package com.dhmoney.userservice.config.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Slf4j
public class OAuthClientCredentialsFeignManager {

    private final OAuth2AuthorizedClientManager manager;
    private final Authentication principal;
    private final ClientRegistration clientRegistration;
    private OAuth2AccessToken token;

    public OAuthClientCredentialsFeignManager(OAuth2AuthorizedClientManager manager, ClientRegistration clientRegistration)
    {
        this.manager = manager;
        this.clientRegistration = clientRegistration;
        this.principal = createPrincipal();
    }

    private Authentication createPrincipal()
    {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return this;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return clientRegistration.getClientId();
            }
        };
    }

    public String getAccessToken()
    {
        if (token == null || Instant.now().isAfter(token.getExpiresAt())) {
            try {
                OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId()).principal(principal).build();
                OAuth2AuthorizedClient client = manager.authorize(oAuth2AuthorizeRequest);
                if (Objects.isNull(client)) {
                    throw new IllegalStateException("client credentials flow on " + clientRegistration.getRegistrationId() + " failed, client is null");
                }
                setToken(client.getAccessToken());
            } catch (Exception exp) {
                log.error("client credentials error: {}", exp.getMessage());
            }
        }
        return (token != null ? token.getTokenValue() : null);
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = token;
    }
}
