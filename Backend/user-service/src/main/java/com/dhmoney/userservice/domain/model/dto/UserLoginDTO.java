package com.dhmoney.userservice.domain.model.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO {

    private String email;
    private String password;
}
