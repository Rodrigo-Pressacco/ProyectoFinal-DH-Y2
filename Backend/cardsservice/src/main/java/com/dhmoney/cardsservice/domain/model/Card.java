package com.dhmoney.cardsservice.domain.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cards", schema = "dhmoney")
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
    private Long id;

  @Column (name = "account_id", nullable = false)
  private Long accountId;

  @Column (name = "card_number", nullable = false)
  private String cardNumber;

  @Column
  private String fullName;

  @Column
  private String type;

  @Column(name = "expiration_date", nullable = false)
  private Date expirationDate;

  @Column (name = "security_code", nullable = false)
  private String securityCode;

  @Column (name = "last_four_digits", nullable = false)
  private String lastFourDigits;

}
