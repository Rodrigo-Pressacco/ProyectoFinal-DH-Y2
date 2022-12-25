package com.dhmoney.userservice.domain.model;

import lombok.*;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserKeycloak {

    private String id;
    private String username;
    private String password;
    private String email;


}
