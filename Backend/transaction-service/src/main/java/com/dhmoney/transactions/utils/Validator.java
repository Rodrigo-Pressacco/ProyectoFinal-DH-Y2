package com.dhmoney.transactions.utils;

import org.springframework.stereotype.Service;

@Service
public class Validator {

  String regexDescription = "^(.|\\s)*[a-zA-Z]+(.|\\s)*$";
  String regexNumber = "^[0-9]*$";

  public Validator() {
  }

  public boolean validateDescription(String description) {
    return description.matches(regexDescription);
  }

  public boolean isNumber(String cvu) {
    return cvu.matches(regexNumber);
  }

}
