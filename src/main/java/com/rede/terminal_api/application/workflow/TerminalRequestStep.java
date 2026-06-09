package com.rede.terminal_api.application.workflow;

import com.rede.terminal_api.domain.model.TerminalRequest;

import java.util.Optional;

public interface TerminalRequestStep {
    WorkflowResult process(TerminalRequest terminalRequest);
    Optional<TerminalRequestStep> nextStep();
}