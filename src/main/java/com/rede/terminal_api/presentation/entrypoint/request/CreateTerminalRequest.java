package com.rede.terminal_api.presentation.entrypoint.request;

import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTerminalRequest(

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotBlank(message = "terminalType is required")
        String terminalType,

        @Valid
        @NotNull(message = "address is required")
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