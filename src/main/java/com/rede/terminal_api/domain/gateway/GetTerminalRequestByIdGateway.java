package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.util.Optional;
import java.util.UUID;

public interface GetTerminalRequestGateway {
    Optional<TerminalRequest> execute(UUID id);
}
