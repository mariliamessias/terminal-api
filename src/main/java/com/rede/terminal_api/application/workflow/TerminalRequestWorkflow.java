package com.rede.terminal_api.application.workflow;

import com.rede.terminal_api.application.workflow.steps.ValidateCustomerStep;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TerminalRequestWorkflow {

    private final SaveTerminalRequestGateway saveGateway;
    private final ValidateCustomerStep validateCustomerStep;

    public void execute(TerminalRequest terminalRequest) {
        executeStep(validateCustomerStep, terminalRequest);
    }

    private void executeStep(
            TerminalRequestStep step,
            TerminalRequest terminalRequest
    ) {
        var result = step.process(terminalRequest);
        saveGateway.execute(terminalRequest);
        executeNexStep(step, terminalRequest, result);
    }

    private void executeNexStep(TerminalRequestStep step, TerminalRequest terminalRequest, WorkflowResult result) {
        if (result.shouldContinue()) {
            step.nextStep()
                    .ifPresent(next -> executeStep(next, terminalRequest));
        }
    }
}