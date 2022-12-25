package com.dhmoney.userservice.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/*@AllArgsConstructor
public class MyRequestInterceptor implements RequestInterceptor {

    private String token;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // validate and refresh your token, this sample is not thread safe
        if (LocalDateTime.now().isAfter(expirationDate)) {
            requestToken();
        }

        // use the token
        requestTemplate.header("Authorization", "Bearer " + token);
    }
}*/