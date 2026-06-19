package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.util.Optional;

public interface GetTerminalRequestByExternalKeyGateway {
    Optional<TerminalRequest> execute(String externalKey);
}
