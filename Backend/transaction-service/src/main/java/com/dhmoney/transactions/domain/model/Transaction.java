package com.dhmoney.transactions.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions", schema = "dhmoney")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private Double amount;

    @Column
    @CreationTimestamp
    private Date dated;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(length = 32)
    private String type;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_id_destination")
    private Long accountIdDestination;

    @Column(name = "card_id")
    private Long cardID;

    @Column(length = 32)
    private String origin;

    @Column(length = 32)
    private String destination;

    @Column(nullable = true)
    private String nameOrigin;

    @Column(nullable = true)
    private String nameDestination;
}
