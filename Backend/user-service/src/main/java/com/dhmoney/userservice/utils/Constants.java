package com.dhmoney.userservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    public static String keycloakPort;
    public static String serverUrl;
    public static String realm;
    public static String clientId;
    public static String clientSecret;
    public static String keycloakUserInfoURL;

    @Value("${dh.keycloak.port}")
    public void setKeycloakURLs(String keycloakPort){
        Constants.keycloakPort = keycloakPort;
        Constants.keycloakUserInfoURL = "http://localhost:"+keycloakPort+"/realms/dhmoney/protocol/openid-connect/userinfo";
    }

    @Value("${dh.keycloak.serverUrl}")
    public void setServerUrl(String serverUrl) {
        Constants.serverUrl = serverUrl;
    }

    @Value("${dh.keycloak.realm}")
    public void setRealm(String realm) {
        Constants.realm = realm;
    }

    @Value("${dh.keycloak.clientId}")
    public void setClientId(String clientId) {
        Constants.clientId = clientId;
    }

    @Value("${dh.keycloak.clientSecret}")
    public void setClientSecret(String clientSecret) {
        Constants.clientSecret = clientSecret;
    }

}
