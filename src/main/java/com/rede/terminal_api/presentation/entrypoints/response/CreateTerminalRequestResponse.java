package com.rede.terminal_api.presentation.entrypoints.response;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTerminalRequestResponse(
        UUID id,
        String customerId,
        String terminalType,
        String status,
        LocalDateTime createdAt
) {

    public static CreateTerminalRequestResponse from(
            TerminalRequest request
    ) {

        return new CreateTerminalRequestResponse(
                request.getId(),
                request.getCustomerId(),
                request.getTerminalType().name(),
                request.getStatus().name(),
                request.getCreatedAt()
        );

    }

}