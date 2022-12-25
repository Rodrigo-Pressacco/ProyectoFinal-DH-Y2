package com.dhmoney.restassuretest.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDTO {

    private String email;
    private String firstName;
    private String lastName;
    private Long dni;
    private String phone;
    private String password;
}
