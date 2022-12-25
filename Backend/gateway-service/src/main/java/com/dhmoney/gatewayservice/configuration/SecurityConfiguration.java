package com.dhmoney.gatewayservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception
    {
        http.cors().and().csrf().disable()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

}
