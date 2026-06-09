package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.TerminalRequest;

public interface SaveTerminalRequestGateway {
    TerminalRequest execute(TerminalRequest request);
}
