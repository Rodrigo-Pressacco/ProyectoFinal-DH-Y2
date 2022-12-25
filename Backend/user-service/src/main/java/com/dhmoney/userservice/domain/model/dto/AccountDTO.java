package com.dhmoney.userservice.domain.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {

    private String cvu;
    private String alias;
}
