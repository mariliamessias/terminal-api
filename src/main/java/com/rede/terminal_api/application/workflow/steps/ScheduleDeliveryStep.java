package com.rede.terminal_api.application.workflow.steps;


import com.rede.terminal_api.application.workflow.TerminalRequestStep;
import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.gateway.ScheduleLogisticsGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleDeliveryStep implements TerminalRequestStep {

    private final ScheduleLogisticsGateway scheduleLogisticsGateway;

    @Override
    public WorkflowResult process(TerminalRequest terminalRequest) {
        var schedule = scheduleLogisticsGateway.execute(terminalRequest);

        if (schedule.isEmpty()) {
            terminalRequest.failScheduling();
            return WorkflowResult.STOP;
        }

        terminalRequest.scheduleDelivery();
        return WorkflowResult.CONTINUE;
    }

    @Override
    public Optional<TerminalRequestStep> nextStep() {
        return Optional.empty();
    }
}