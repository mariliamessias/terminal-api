package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.domain.model.TerminalRequest;

public interface CreateTerminalRequestUseCase {
    TerminalRequest execute(TerminalRequest request);
}
