package com.rede.terminal_api.application.workflow.steps;

import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.domain.gateway.ScheduleLogisticsGateway;
import com.rede.terminal_api.domain.model.DeliverySchedule;
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
class ScheduleDeliveryStepTest {

    @Mock
    private ScheduleLogisticsGateway gateway;

    @InjectMocks
    private ScheduleDeliveryStep step;

    @Test
    void shouldScheduleDeliveryWhenScheduleExists() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(gateway.execute(terminalRequest))
                .thenReturn(Optional.of(new DeliverySchedule("SCH-1")));

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.CONTINUE, result);
        assertEquals(AGENDADO, terminalRequest.getStatus());
    }

    @Test
    void shouldFailSchedulingWhenScheduleDoesNotExist() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "AM");

        when(gateway.execute(terminalRequest))
                .thenReturn(Optional.empty());

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.STOP, result);
        assertEquals(ERRO_AGENDAMENTO, terminalRequest.getStatus());
    }

    @Test
    void shouldNotHaveNextStep() {
        // when
        var nextStep = step.nextStep();

        // then
        assertTrue(nextStep.isEmpty());
    }

    @Test
    void shouldKeepValidReservedWhenLogisticsIntegrationFails() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "RR");
        terminalRequest.validateCustomer();
        terminalRequest.reserveTerminal();

        when(gateway.execute(terminalRequest))
                .thenThrow(new IntegrationUnavailableException("Logistics scheduling service unavailable"));

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> step.process(terminalRequest)
        );

        assertEquals(RESERVADO, terminalRequest.getStatus());
    }
}