package com.dhmoney.restassuretest.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String firstname;
    private String lastname;
    private String dni;
    private String email;
    private String phone;
}
