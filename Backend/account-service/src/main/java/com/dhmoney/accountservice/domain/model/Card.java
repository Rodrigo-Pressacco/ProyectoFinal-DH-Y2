package com.dhmoney.accountservice.domain.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    private Long accountId;
    private String cardNumber;
    private String fullName;
    private String expirationDate;
    private String securityCode;

}