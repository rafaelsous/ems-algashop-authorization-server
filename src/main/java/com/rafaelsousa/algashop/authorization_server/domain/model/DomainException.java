package com.rafaelsousa.algashop.authorization_server.domain.model;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}