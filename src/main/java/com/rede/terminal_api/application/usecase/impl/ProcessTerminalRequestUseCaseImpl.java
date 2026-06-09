package com.rede.terminal_api.application.usecase.impl;

import com.rede.terminal_api.application.usecase.ProcessTerminalRequestUseCase;
import com.rede.terminal_api.application.workflow.TerminalRequestWorkflow;
import com.rede.terminal_api.domain.exception.TerminalRequestNotFoundException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessTerminalRequestUseCaseImpl implements ProcessTerminalRequestUseCase {

    private final GetTerminalRequestGateway getTerminalRequestGateway;
    private final TerminalRequestWorkflow terminalRequestWorkflow;

    @Override
    public void execute(UUID terminalRequestId) {
        var terminalRequest = getTerminalRequestGateway.execute(terminalRequestId)
                .orElseThrow(() -> new TerminalRequestNotFoundException(terminalRequestId));

        terminalRequestWorkflow.execute(terminalRequest);

    }
}
