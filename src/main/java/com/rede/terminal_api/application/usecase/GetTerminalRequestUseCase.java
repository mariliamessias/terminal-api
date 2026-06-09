package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.util.UUID;

public interface GetTerminalRequestUseCase {
    TerminalRequest execute(UUID id);
}
