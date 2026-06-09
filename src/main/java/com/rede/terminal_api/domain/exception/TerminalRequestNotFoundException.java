package com.rede.terminal_api.domain.exception;

import java.util.UUID;

public class TerminalRequestNotFoundException extends RuntimeException {
    public TerminalRequestNotFoundException(UUID id) {
        super("Terminal request not found: " + id);
    }

}