package com.dhmoney.userservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Validator {

    String regexName = "[a-zA-ZÀ-ÖØ-öø-ÿ]+\\.?(( |\\-)[a-zA-ZÀ-ÖØ-öø-ÿ]+\\.?)*";
    String regexEmail = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\" +
            ".[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
    String regexPhone = "0{0,2}([\\+]?[\\d]{1,3} ?)?([\\(]([\\d]{2,3})[)] ?)" +
            "?[0-9][0-9 \\-]{6,}( ?([xX]|([eE]xt[\\.]?)) ?([\\d]{1,5}))?";
    String regexPassword = "^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{6,20}$";

    public Validator() {
    }

    public boolean validateNames(String name) {
        boolean passed = name.matches(regexName);
        log.info("Validate name: " + name + " passed: " + passed);
        return passed;
    }

    public boolean validateEmail(String email) {
        boolean passed = email.matches(regexEmail);
        log.info("Validate email: " + email + " passed: " + passed);
        return passed;
    }

    public boolean validatePhone(String phone) {
        boolean passed = phone.matches(regexPhone);
        log.info("Validate phone: " + phone + " passed: " + passed);
        return passed;
    }

    public boolean validatePassword(String password) {
        boolean passed = password.matches(regexPassword);
        log.info("Validate password: " + password + " passed: " + passed);
        return passed;
    }
}
