package com.rede.terminal_api.application.workflow.steps;


import com.rede.terminal_api.application.workflow.TerminalRequestStep;
import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.gateway.ReserveTerminalGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReserveTerminalStep implements TerminalRequestStep {

    private final ReserveTerminalGateway reserveTerminalGateway;
    private final ScheduleDeliveryStep scheduleDeliveryStep;

    @Override
    public boolean supports(TerminalRequestStatus status) {
        return status == TerminalRequestStatus.VALIDADO;
    }

    @Override
    public WorkflowResult process(TerminalRequest terminalRequest) {
        var reservation = reserveTerminalGateway.execute(
                terminalRequest.getCustomerId(),
                terminalRequest.getTerminalType()
        );

        if (reservation.isEmpty()) {
            terminalRequest.failReservation();
            return WorkflowResult.STOP;
        }

        terminalRequest.reserveTerminal();
        return WorkflowResult.CONTINUE;
    }

    @Override
    public Optional<TerminalRequestStep> nextStep() {
        return Optional.of(scheduleDeliveryStep);
    }
}
