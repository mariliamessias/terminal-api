package com.rede.terminal_api.application.workflow.steps;

import com.rede.terminal_api.application.workflow.WorkflowResult;
import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.domain.gateway.ReserveTerminalGateway;
import com.rede.terminal_api.domain.model.TerminalReservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.rede.terminal_api.domain.model.TerminalRequestStatus.*;
import static com.rede.terminal_api.domain.model.TerminalType.POS_CHIP;
import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveTerminalStepTest {

    @Mock
    private ReserveTerminalGateway gateway;

    @Mock
    private ScheduleDeliveryStep scheduleDeliveryStep;

    @InjectMocks
    private ReserveTerminalStep step;

    @Test
    void shouldReserveTerminalWhenReservationExists() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(gateway.execute("CUST-VALID", POS_WIFI))
                .thenReturn(Optional.of(new TerminalReservation("RES-1")));

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.CONTINUE, result);
        assertEquals(RESERVADO, terminalRequest.getStatus());
    }

    @Test
    void shouldFailReservationWhenReservationDoesNotExist() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(gateway.execute("CUST-VALID", POS_WIFI))
                .thenReturn(Optional.empty());

        // when
        var result = step.process(terminalRequest);

        // then
        assertEquals(WorkflowResult.STOP, result);
        assertEquals(ERRO_RESERVA, terminalRequest.getStatus());
    }

    @Test
    void shouldReturnScheduleDeliveryAsNextStep() {
        // when
        var nextStep = step.nextStep();

        // then
        assertTrue(nextStep.isPresent());
        assertEquals(scheduleDeliveryStep, nextStep.get());
    }

    @Test
    void shouldKeepValidReserveWhenReservationIntegrationFails() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_CHIP, "SP");
        terminalRequest.validateCustomer();

        when(gateway.execute("CUST-VALID", POS_CHIP))
                .thenThrow(new IntegrationUnavailableException("Terminal reservation service unavailable"));

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> step.process(terminalRequest)
        );

        assertEquals(VALIDADO, terminalRequest.getStatus());
    }
}