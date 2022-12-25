package com.dhmoney.cardsservice.service;

import com.dhmoney.cardsservice.domain.model.Card;
import com.dhmoney.cardsservice.domain.model.dto.CardDTO;
import com.dhmoney.cardsservice.domain.model.dto.CardPublicDTO;
import com.dhmoney.cardsservice.exception.*;

import java.util.List;

public interface ICardService {

  CardPublicDTO saveCard(CardDTO cardDTO) throws ResourceConflictException, ResourceBadRequestException, ResourceEmptyField;
  void deleteCard(Long id, Long accountId) throws ResourceNotFoundException;
  CardPublicDTO getCardById(Long id, Long accountId) throws ResourceNotFoundException;
  List<CardPublicDTO> getAllCardsByAccount(Long accountId);
}
