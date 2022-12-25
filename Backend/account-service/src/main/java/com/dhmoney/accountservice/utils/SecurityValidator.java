package com.dhmoney.accountservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class SecurityValidator {

    public boolean isUserTheAccountOwner(Long id, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userIdInToken = jwt.getClaimAsString("user_id");

        return (id == Long.parseLong(userIdInToken));
    }

    public boolean isUserAnotherService(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userIdInToken = jwt.getClaimAsString("user_id");

        return (userIdInToken == null);
    }
}
