package com.dhmoney.accountservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Validator {

    String regexAlias = "[a-zA-ZÀ-ÖØ-öø-ÿ0-9\\.]{5,19}$";
    String regexDescription = "^(.|\\s)*[a-zA-Z]+(.|\\s)*$";
    String regexNumber = "^[0-9]*$";

    public Validator() {
    }

    public boolean validateAlias(String alias) {
        boolean passed = alias.matches(regexAlias);
        log.info("Validate alias: " + alias + " passed: " + passed);
        return passed;
    }

    public boolean validateDescription(String description) {
        return description.matches(regexDescription);
    }

    public boolean isNumber(String cvu) {
        return cvu.matches(regexNumber);
    }
}
