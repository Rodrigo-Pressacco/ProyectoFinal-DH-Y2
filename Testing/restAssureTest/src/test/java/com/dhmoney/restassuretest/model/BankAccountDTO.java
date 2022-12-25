package com.dhmoney.restassuretest.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDTO {

    private String cvu;
    private String alias;
}