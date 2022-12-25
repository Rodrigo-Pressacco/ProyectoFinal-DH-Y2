package com.dhmoney.accountservice.domain.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {

    private Long id;
    private String cvu;
    private String alias;
    private Long userId;
    private Double balance;
    private String name;
}
