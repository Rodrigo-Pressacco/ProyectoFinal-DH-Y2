package com.dhmoney.cardsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class OAuth2ResourceServerSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        /*JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());*/

        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/swagger-ui.html", "/swagger-resources/", "/webjars/", "/swagger-ui/index.html", "/v3/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        //.jwtAuthenticationConverter(jwtAuthenticationConverter);
        return http.build();
    }

    /*@Bean
    public JwtDecoder jwtDecoder()
    {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/realms/dhmoney/protocol/openid-connect/certs").build();
    }*/
}