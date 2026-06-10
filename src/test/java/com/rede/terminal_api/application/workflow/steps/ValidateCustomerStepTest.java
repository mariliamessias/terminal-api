package com.rede.terminal_api.application.workflow.steps;

import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.domain.gateway.GetCustomerInfoGateway;
import com.rede.terminal_api.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.rede.terminal_api.domain.model.TerminalRequestStatus.*;
import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateCustomerStepTest {

    @Mock
    private GetCustomerInfoGateway gateway;

    @Mock
    private ReserveTerminalStep reserveTerminalStep;

    @InjectMocks
    private ValidateCustomerStep step;

    @Test
    void shouldValidateCustomerWhenCustomerExistsAndIsActive() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(gateway.execute("CUST-VALID"))
                .thenReturn(Optional.of(new Customer("CUST-VALID", true)));

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.CONTINUE, result);
        assertEquals(VALIDADO, terminalRequest.getStatus());
    }

    @Test
    void shouldRejectCustomerWhenCustomerDoesNotExist() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-NOT-FOUND", POS_WIFI, "SP");

        when(gateway.execute("CUST-NOT-FOUND"))
                .thenReturn(Optional.empty());

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.STOP, result);
        assertEquals(REJEITADO, terminalRequest.getStatus());
    }

    @Test
    void shouldRejectCustomerWhenCustomerIsInactive() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-INACTIVE", POS_WIFI, "SP");

        when(gateway.execute("CUST-INACTIVE"))
                .thenReturn(Optional.of(new Customer("CUST-INACTIVE", false)));

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.STOP, result);
        assertEquals(REJEITADO, terminalRequest.getStatus());
    }

    @Test
    void shouldReturnReserveTerminalAsNextStep() {
        // when
        var nextStep = step.nextStep();

        // then
        assertTrue(nextStep.isPresent());
        assertEquals(reserveTerminalStep, nextStep.get());
    }

    @Test
    void shouldKeepValidRequiredWhenCustomerIntegrationFails() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-INTEGRATION-FAIL", POS_WIFI, "SP");

        when(gateway.execute("CUST-INTEGRATION-FAIL"))
                .thenThrow(new IntegrationUnavailableException("Customer service unavailable"));

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> step.process(terminalRequest)
        );

        assertEquals(SOLICITADO, terminalRequest.getStatus());
    }
}