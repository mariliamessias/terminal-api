package com.rede.terminal_api.domain.exception;

public class IntegrationUnavailableException extends RuntimeException {
    public IntegrationUnavailableException(String message) {
        super(message);
    }
}
