package com.rede.terminal_api.application.workflow;

import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalRequestStatus;

import java.util.Optional;

public interface TerminalRequestStep {
    boolean supports(TerminalRequestStatus status);

    WorkflowResult process(TerminalRequest terminalRequest);

    Optional<TerminalRequestStep> nextStep();
}