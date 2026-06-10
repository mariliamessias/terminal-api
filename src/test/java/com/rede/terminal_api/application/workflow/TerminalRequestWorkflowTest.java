package com.rede.terminal_api.application.workflow;


import com.rede.terminal_api.application.workflow.steps.ValidateCustomerStep;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerminalRequestWorkflowTest {

    @Mock
    private SaveTerminalRequestGateway saveGateway;

    @Mock
    private ValidateCustomerStep validateCustomerStep;

    @Mock
    private TerminalRequestStep reserveTerminalStep;

    @Mock
    private TerminalRequestStep scheduleDeliveryStep;

    @InjectMocks
    private TerminalRequestWorkflow workflow;

    @Test
    void shouldExecuteFirstStepAndSaveWhenStepStopsWorkflow() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(validateCustomerStep.process(terminalRequest))
                .thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(terminalRequest);

        // then
        verify(validateCustomerStep).process(terminalRequest);
        verify(saveGateway).execute(terminalRequest);
        verify(validateCustomerStep, never()).nextStep();

        verifyNoInteractions(reserveTerminalStep);
        verifyNoInteractions(scheduleDeliveryStep);
    }

    @Test
    void shouldExecuteNextStepWhenResultIsContinue() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(validateCustomerStep.process(terminalRequest))
                .thenReturn(WorkflowResult.CONTINUE);

        when(validateCustomerStep.nextStep())
                .thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.process(terminalRequest))
                .thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(terminalRequest);

        // then
        InOrder inOrder = inOrder(
                validateCustomerStep,
                reserveTerminalStep,
                saveGateway
        );

        inOrder.verify(validateCustomerStep).process(terminalRequest);
        inOrder.verify(saveGateway).execute(terminalRequest);
        inOrder.verify(validateCustomerStep).nextStep();
        inOrder.verify(reserveTerminalStep).process(terminalRequest);
        inOrder.verify(saveGateway).execute(terminalRequest);

        verify(reserveTerminalStep, never()).nextStep();
        verify(saveGateway, times(2)).execute(terminalRequest);
    }

    @Test
    void shouldExecuteAllStepsWhileResultIsContinue() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(validateCustomerStep.process(terminalRequest))
                .thenReturn(WorkflowResult.CONTINUE);

        when(validateCustomerStep.nextStep())
                .thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.process(terminalRequest))
                .thenReturn(WorkflowResult.CONTINUE);

        when(reserveTerminalStep.nextStep())
                .thenReturn(Optional.of(scheduleDeliveryStep));

        when(scheduleDeliveryStep.process(terminalRequest))
                .thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(terminalRequest);

        // then
        InOrder inOrder = inOrder(
                validateCustomerStep,
                reserveTerminalStep,
                scheduleDeliveryStep,
                saveGateway
        );

        inOrder.verify(validateCustomerStep).process(terminalRequest);
        inOrder.verify(saveGateway).execute(terminalRequest);
        inOrder.verify(validateCustomerStep).nextStep();

        inOrder.verify(reserveTerminalStep).process(terminalRequest);
        inOrder.verify(saveGateway).execute(terminalRequest);
        inOrder.verify(reserveTerminalStep).nextStep();

        inOrder.verify(scheduleDeliveryStep).process(terminalRequest);
        inOrder.verify(saveGateway).execute(terminalRequest);

        verify(scheduleDeliveryStep, never()).nextStep();
        verify(saveGateway, times(3)).execute(terminalRequest);
    }

    @Test
    void shouldStopWhenResultIsContinueButNextStepDoesNotExist() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(validateCustomerStep.process(terminalRequest))
                .thenReturn(WorkflowResult.CONTINUE);

        when(validateCustomerStep.nextStep())
                .thenReturn(Optional.empty());

        // when
        workflow.execute(terminalRequest);

        // then
        verify(validateCustomerStep).process(terminalRequest);
        verify(saveGateway).execute(terminalRequest);
        verify(validateCustomerStep).nextStep();

        verifyNoInteractions(reserveTerminalStep);
        verifyNoInteractions(scheduleDeliveryStep);
    }
}