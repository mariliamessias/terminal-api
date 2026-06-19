package com.rede.terminal_api.application.usecase.impl;

import com.rede.terminal_api.application.usecase.GetTerminalRequestUseCase;
import com.rede.terminal_api.domain.exception.TerminalRequestNotFoundException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestByIdGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTerminalRequestUseCaseImpl implements GetTerminalRequestUseCase {

    private final GetTerminalRequestByIdGateway getTerminalRequestByIdGateway;

    @Override
    public TerminalRequest execute(UUID id) {
        return getTerminalRequestByIdGateway.execute(id)
                .orElseThrow(() -> new TerminalRequestNotFoundException(id));
    }
}
