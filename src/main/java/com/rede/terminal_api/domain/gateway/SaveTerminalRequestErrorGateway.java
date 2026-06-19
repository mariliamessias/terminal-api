package com.rede.terminal_api.domain.gateway;

import java.util.UUID;

public interface SaveTerminalRequestErrorGateway {
    void saveOrUpdate(UUID terminalRequestId, String errorMessage);

    void clear(UUID terminalRequestId);
}
