package com.dhmoney.restassuretest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDto {

    private Long accountId;
    private String number;
    private String name;
    private String expiration;
    private String cvc;
}
