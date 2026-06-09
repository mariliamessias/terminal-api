package com.rede.terminal_api.presentation.entrypoints.request;

import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalType;

public record CreateTerminalRequest(
        String customerId,
        String terminalType,
        AddressRequest address
) {

    public TerminalRequest toDomain() {
        return TerminalRequest.create(
                customerId,
                TerminalType.valueOf(terminalType),
                address.toDomain()
        );
    }
}