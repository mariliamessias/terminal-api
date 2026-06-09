package com.rede.terminal_api.application.usecase.impl;

import com.rede.terminal_api.application.usecase.GetTerminalRequestUseCase;
import com.rede.terminal_api.domain.exception.TerminalRequestNotFoundException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTerminalRequestUseCaseImpl implements GetTerminalRequestUseCase {

    private final GetTerminalRequestGateway getTerminalRequestGateway;

    @Override
    public TerminalRequest execute(UUID id) {
        return getTerminalRequestGateway.execute(id)
                .orElseThrow(() -> new TerminalRequestNotFoundException(id));
    }
}
