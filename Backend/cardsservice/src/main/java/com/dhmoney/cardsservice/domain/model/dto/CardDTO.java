package com.dhmoney.cardsservice.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDTO {

  private Long accountId;

  @JsonProperty("number")
  private String cardNumber;

  @JsonProperty("name")
  private String fullName;

  @JsonProperty("expiration")
  private String expirationDate;

  @JsonProperty("cvc")
  private String securityCode;

}
