package com.dhmoney.accountservice.domain.model.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardPublicDTO {

    private String id;

    private Long accountId;

    @JsonProperty("name")
    private String fullName;

    @JsonProperty("number")
    private String cardNumber;

    private String type;

}
