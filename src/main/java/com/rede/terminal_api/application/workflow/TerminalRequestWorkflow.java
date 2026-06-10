package com.rede.terminal_api.application.workflow;

import com.rede.terminal_api.application.workflow.steps.ValidateCustomerStep;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TerminalRequestWorkflow {

    private final SaveTerminalRequestGateway saveGateway;
    private final ValidateCustomerStep firstStep;

    public void execute(TerminalRequest terminalRequest) {
        executeStep(firstStep, terminalRequest);
    }

    private void executeStep(TerminalRequestStep requestStep, TerminalRequest request) {
        Optional.ofNullable(requestStep).ifPresent(step -> {
            WorkflowResult result = WorkflowResult.CONTINUE;

            if (step.supports(request.getStatus())) {
                result = step.process(request);
                saveGateway.execute(request);
            }

            if (result.shouldContinue()) {
                executeStep(step.nextStep().orElse(null), request);
            }
        });
    }

}