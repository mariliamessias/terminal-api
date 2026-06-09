package com.rede.terminal_api.application.usecase;

import java.util.UUID;

public interface ProcessTerminalRequestUseCase {
    void execute(UUID terminalRequestId);
}
