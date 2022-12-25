package com.dhmoney.userservice.config.feign;

import com.dhmoney.userservice.utils.RetrieveMessageErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

//Esta clase ya no es necesaria porque este Bean se declara en OAuthFeignConfiguration
/*public class FeignClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }
}*/