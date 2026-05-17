package com.rafaelsousa.algashop.authorization_server.application.user.management;

public class AuthUserEmailAlreadyInUseException extends RuntimeException {
    public AuthUserEmailAlreadyInUseException(String emailAlreadyExists) {
        super("Email already in use " + emailAlreadyExists);
    }
}