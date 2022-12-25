package com.dhmoney.cardsservice.service.impl;

import com.dhmoney.cardsservice.domain.model.Card;
import com.dhmoney.cardsservice.domain.model.dto.CardDTO;
import com.dhmoney.cardsservice.domain.model.dto.CardPublicDTO;
import com.dhmoney.cardsservice.domain.repository.CardRepository;
import com.dhmoney.cardsservice.exception.ResourceBadRequestException;
import com.dhmoney.cardsservice.exception.ResourceConflictException;
import com.dhmoney.cardsservice.exception.ResourceEmptyField;
import com.dhmoney.cardsservice.exception.ResourceNotFoundException;
import com.dhmoney.cardsservice.service.ICardService;
import com.dhmoney.cardsservice.utils.CardValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CardService implements ICardService {

  private final CardRepository repository;
  private final CardValidator validator;
  ObjectMapper mapper;
  ModelMapper modelMapper;

  @Autowired
  public CardService(
          CardRepository repository,
          ObjectMapper mapper,
          CardValidator validator,
          ModelMapper modelMapper) {
    this.repository = repository;
    this.validator = validator;
    this.mapper = mapper;
    this.modelMapper = modelMapper;
  }

  @Override
  public CardPublicDTO getCardById(Long id, Long accountId) throws ResourceNotFoundException {
    CardPublicDTO cardPublicDTO = null;
    log.info("Retrieving card with ID " + id + " for account with ID " + accountId);
    Card card = repository.getCardByIdAndAccount(id, accountId);
    if (card == null) {
      throw new ResourceNotFoundException("The card doesn't exist for the account");
    }
    cardPublicDTO =  modelMapper.map(card, CardPublicDTO.class);
    return cardPublicDTO;
  }


  @Override
  public List<CardPublicDTO> getAllCardsByAccount(Long accountId) {
    log.info("Retrieving all cards for account with ID " + accountId);
    List<Card> listCards = repository.getCardsByAccount(accountId);
    return modelMapper.map(listCards,
            new TypeToken<List<CardPublicDTO>>() {}.getType());
  }

  @Override
  public CardPublicDTO saveCard(CardDTO cardDTO) throws ResourceEmptyField, ResourceConflictException, ResourceBadRequestException {
    log.info("Validating card data");
    validateEmptityCard(cardDTO);

    // Validación de fecha de expiración
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
    simpleDateFormat.setLenient(false);
    Date dateExpiration;
    try {
      dateExpiration = simpleDateFormat.parse(cardDTO.getExpirationDate());
    } catch (ParseException error) {
      throw new ResourceBadRequestException("The expiration date is invalid");
    }
    validateStructure(cardDTO, dateExpiration);

    String card;
    log.info("Retrieving card with number " + cardDTO.getCardNumber());
    card = repository.existingCardByUser(cardDTO.getCardNumber());

    if (card != null) {
      throw new ResourceConflictException("A card with the same number already exists");
    }

    int last = cardDTO.getCardNumber().length();
    int first = last - 4;
    String lastFour = cardDTO.getCardNumber().substring(first, last);

    Card cardToSave = Card.builder()
            .accountId(cardDTO.getAccountId())
            .cardNumber(cardDTO.getCardNumber())
            .fullName(cardDTO.getFullName())
            .type(validator.getCardType(cardDTO.getCardNumber()))
            .expirationDate(dateExpiration)
            .securityCode(cardDTO.getSecurityCode())
            .lastFourDigits(lastFour)
            .build();

    log.info("Saving card to local DB");
    Card cardSaved = repository.save(cardToSave);
    log.info("A card with ID " + cardSaved.getId() + " was created.");
    CardPublicDTO cardPublicDTO = modelMapper.map(cardSaved, CardPublicDTO.class);

    return cardPublicDTO;
  }

  @Override
  public void deleteCard(Long id, Long accountId) throws ResourceNotFoundException {
    log.info("Deleting card with ID " + id + " for account with ID " + accountId);
    Card card = repository.findById(id).orElse(null);

    if (card == null) {
      throw new ResourceNotFoundException("The card doesn't exist for the account");
    }

    repository.deleteById(id);
    log.info("Card with ID " + id + " for account with ID " + accountId + " was deleted");
  }

  private void validateEmptityCard(CardDTO cardDTO) throws ResourceEmptyField {
    //Empty validations
    if (cardDTO.getCardNumber() == null){
      throw new ResourceEmptyField("Card number cannot be empty");
    }
    if (cardDTO.getExpirationDate() == null){
      throw new ResourceEmptyField("Expiration date cannot be empty");
    }
    if(cardDTO.getSecurityCode() == null){
      throw new ResourceEmptyField("Security code cannot be empty");
    }
    if(cardDTO.getFullName() == null){
      throw new ResourceEmptyField("Full name field cannot be empty");
    }
  }

  private void validateStructure(CardDTO cardDTO, Date dateExpiration) throws ResourceBadRequestException {
    if (!validator.isNumber(cardDTO.getCardNumber())) {
      throw new ResourceBadRequestException("The cardNumber is " +
              "invalid");
    }

    if (!validator.isNumber(cardDTO.getSecurityCode())) {
      throw new ResourceBadRequestException("The security code is " +
              "invalid");
    }

    if (!validator.validateFullname(cardDTO.getFullName())) {
      throw new ResourceBadRequestException("The name is " +
              "invalid");
    }

    if (!validator.validateLengthNumberCard(cardDTO.getCardNumber())) {
      throw new ResourceBadRequestException("The cardNumber is " +
              "invalid");
    }

    if (!validator.validateLengthSecurityNumber(cardDTO.getSecurityCode())) {
      throw new ResourceBadRequestException("The security code is " +
              "invalid");
    }

    if (dateExpiration.before(new Date())) {
      throw new ResourceBadRequestException("Your card is expired");
    }
  }

}