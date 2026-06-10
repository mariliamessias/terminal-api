package com.rede.terminal_api.application.workflow.steps;

import com.rede.terminal_api.application.workflow.TerminalRequestStep;
import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.gateway.GetCustomerInfoGateway;
import com.rede.terminal_api.domain.model.Customer;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ValidateCustomerStep implements TerminalRequestStep {

    private final GetCustomerInfoGateway getCustomerInfoGateway;
    private final ReserveTerminalStep reserveTerminalStep;

    @Override
    public boolean supports(TerminalRequestStatus status) {
        return status == TerminalRequestStatus.SOLICITADO;
    }

    @Override
    public WorkflowResult process(TerminalRequest terminalRequest) {
        if (isValidCustomer(terminalRequest)) {
            terminalRequest.validateCustomer();
            return WorkflowResult.CONTINUE;
        }

        terminalRequest.rejectCustomer();
        return WorkflowResult.STOP;
    }

    private boolean isValidCustomer(TerminalRequest terminalRequest) {
        return getCustomerInfoGateway.execute(terminalRequest.getCustomerId())
                .filter(Customer::active)
                .isPresent();
    }

    @Override
    public Optional<TerminalRequestStep> nextStep() {
        return Optional.of(reserveTerminalStep);
    }
}