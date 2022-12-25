package com.dhmoney.userservice.domain.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "users", schema = "dhmoney")
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String iamId;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String dni;
    @Column
    private String email;
    @Column
    private String phone;
}
