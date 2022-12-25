package com.dhmoney.cardsservice.domain.repository;

import com.dhmoney.cardsservice.domain.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>  {
  //List<CardDTO> findByAccountId(String accountId);
  @Query( nativeQuery = true, value = "SELECT c.card_number FROM cards AS c"+
          " WHERE c.card_number = ?1 ")
  String existingCardByUser(String cardNumber);

  @Query( nativeQuery = true, value = "SELECT * FROM cards AS c"+
          " WHERE c.account_id = ?1")
  List<Card> getCardsByAccount(Long id);

  @Query( nativeQuery = true, value = "SELECT * FROM cards AS c"+
          " WHERE c.id = ?1 AND c.account_id = ?2")
  Card getCardByIdAndAccount(Long id, Long accountId);


}
