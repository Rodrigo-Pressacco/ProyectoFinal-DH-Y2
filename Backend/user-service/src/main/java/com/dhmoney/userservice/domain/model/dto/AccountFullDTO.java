package com.dhmoney.userservice.domain.model.dto;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountFullDTO {

    private Long id;
    private String cvu;
    private String alias;
    private Long userId;
    private Double balance;
    private String name;
}
