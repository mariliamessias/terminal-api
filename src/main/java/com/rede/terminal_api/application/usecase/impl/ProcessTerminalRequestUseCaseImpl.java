package com.rede.terminal_api.application.usecase.impl;

import com.rede.terminal_api.application.usecase.ProcessTerminalRequestUseCase;
import com.rede.terminal_api.application.workflow.TerminalRequestWorkflow;
import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestByIdGateway;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestErrorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTerminalRequestUseCaseImpl implements ProcessTerminalRequestUseCase {

    private final GetTerminalRequestByIdGateway getTerminalRequestByIdGateway;
    private final SaveTerminalRequestErrorGateway saveTerminalRequestErrorGateway;
    private final TerminalRequestWorkflow terminalRequestWorkflow;

    @Override
    public void execute(UUID eventId, UUID terminalRequestId) {
        var terminalRequestOptional = getTerminalRequestByIdGateway.execute(terminalRequestId);

        if (terminalRequestOptional.isEmpty()) {
            log.warn("Terminal request not found for async processing. eventId={}, terminalRequestId={}",
                    eventId, terminalRequestId);
            return;
        }

        var terminalRequest = terminalRequestOptional.get();

        try {
            terminalRequestWorkflow.execute(terminalRequest);
        } catch (IntegrationUnavailableException exception) {
            saveTerminalRequestErrorGateway.saveOrUpdate(terminalRequestId, exception.getMessage());
            log.warn(
                    "Technical failure during async processing. eventId={}, terminalRequestId={}, error={}",
                    eventId,
                    terminalRequestId,
                    exception.getMessage()
            );
        }
    }
}
