package com.dhmoney.cardsservice.utils;

import com.dhmoney.cardsservice.domain.model.dto.CardDTO;
import org.springframework.stereotype.Service;

@Service
public class CardValidator {

  String regexName = "[a-zA-ZÀ-ÖØ-öø-ÿ]+\\.?(( |\\-)[a-zA-ZÀ-ÖØ-öø-ÿ]+\\.?)*";
  String regexNumber = "^[0-9]*$";
  String regexVisa = "^4\\d{3}-?\\d{4}-?\\d{4}-?\\d{4}$";
  String regexMastercard = "^5[1-5]\\d{2}-?\\d{4}-?\\d{4}-?\\d{4}$";

  public static final String VISA = "Visa";
  public static final String MASTERCARD = "Mastercard";
  public static final String TARJETA = "Tarjeta";

  public CardValidator() {
  }

  public boolean validateFullname(String name) {
    return name.matches(regexName);
  }

  public boolean isNumber(String cardNumber) {
    return cardNumber.matches(regexNumber);

  }

  public boolean validateLengthNumberCard (String cardNumber) {
    return cardNumber.length() >= 13 && cardNumber.length() <= 19;
  }

  public boolean validateLengthSecurityNumber (String securityNumber) {
    return securityNumber.length() >= 3 && securityNumber.length() <= 4;
  }

  public String getCardType(String cardNumber) {
    if (cardNumber.matches(regexVisa)) {
      return VISA;
    } else if (cardNumber.matches(regexMastercard)) {
      return MASTERCARD;
    } else {
      return TARJETA;
    }
  }

}
