package com.rede.terminal_api.presentation.entrypoints.response;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetTerminalRequestResponse(
        UUID id,
        String customerId,
        String terminalType,
        String status,
        LocalDateTime createdAt
) {

    public static GetTerminalRequestResponse from(
            TerminalRequest request
    ) {
        return new GetTerminalRequestResponse(
                request.getId(),
                request.getCustomerId(),
                request.getTerminalType().name(),
                request.getStatus().name(),
                request.getCreatedAt()

        );
    }

}