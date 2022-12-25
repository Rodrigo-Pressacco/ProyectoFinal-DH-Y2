package com.dhmoney.accountservice.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    private Long id;
    private Double amount;
    private Date dated;
    private String description;
    private String type;
    private Long accountId;
    private Long accountIdDestination;
    private Long cardID;
    private String origin;
    private String destination;
    private String nameOrigin;
    private String nameDestination;
}
