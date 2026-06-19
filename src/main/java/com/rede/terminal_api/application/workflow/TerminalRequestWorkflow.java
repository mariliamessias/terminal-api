package com.rede.terminal_api.application.workflow;

import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TerminalRequestWorkflow {

    private final SaveTerminalRequestGateway saveGateway;
    private final List<TerminalRequestStep> steps;

    public void execute(TerminalRequest terminalRequest) {
        recoverStep(terminalRequest.getStatus())
                .ifPresent(step -> executeStep(step, terminalRequest));
    }

    private void executeStep(TerminalRequestStep step, TerminalRequest request) {
        var result = step.process(request);
        saveGateway.execute(request);

        if (result.shouldContinue()) {
            step.nextStep()
                    .ifPresent(next -> executeStep(next, request));
        }
    }

    private Optional<TerminalRequestStep> recoverStep(
            TerminalRequestStatus status
    ) {
        return steps.stream()
                .filter(step -> step.supports(status))
                .findFirst();
    }
}