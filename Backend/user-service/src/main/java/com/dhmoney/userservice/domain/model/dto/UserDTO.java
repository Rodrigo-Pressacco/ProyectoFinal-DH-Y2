package com.dhmoney.userservice.domain.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String firstName;
    private String lastName;
    private Long dni;
    private String email;
    private String phone;
}
