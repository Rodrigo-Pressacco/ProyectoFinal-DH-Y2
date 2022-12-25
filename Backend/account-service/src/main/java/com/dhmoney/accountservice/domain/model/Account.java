package com.dhmoney.accountservice.domain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "accounts", schema = "dhmoney")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String cvu;
    @Column
    private String alias;
    @Column
    private Long userId;
    @Column
    private Double balance;
    @Column
    private String name;
}
