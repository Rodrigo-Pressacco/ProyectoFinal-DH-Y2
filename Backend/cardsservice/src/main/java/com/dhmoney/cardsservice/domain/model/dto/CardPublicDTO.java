package com.dhmoney.cardsservice.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CardPublicDTO {

  private String id;

  private Long accountId;

  @JsonProperty("name")
  private String fullName;

  @JsonProperty("number")
  private String cardNumber;

  private String type;

}