package com.dhmoney.restassuretest.model;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String dni;
    private String email;
    private String phone;
    private List<BankAccountDTO> bankAccounts;
}
