package com.dhmoney.restassuretest.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Double amount;
    private Date dated;
    private String description;
    private String type;
    private Long accountId;
    private String origin;
    private String destination;
}
