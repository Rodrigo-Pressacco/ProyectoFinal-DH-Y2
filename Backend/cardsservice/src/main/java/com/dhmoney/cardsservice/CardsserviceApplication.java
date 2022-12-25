package com.dhmoney.cardsservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "ms-cards API", version = "1.0", description = "Documentation Cards API v1.0"))
public class CardsserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsserviceApplication.class, args);
	}

}
