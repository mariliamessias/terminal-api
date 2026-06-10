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

import static com.rede.terminal_api.domain.model.TerminalRequestStatus.*;
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
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        // given
        when(validateCustomerStep.supports(SOLICITADO)).thenReturn(true);
        when(validateCustomerStep.process(request)).thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(request);

        // then
        verify(validateCustomerStep).process(request);
        verify(saveGateway).execute(request);
        verify(validateCustomerStep, never()).nextStep();
        verifyNoInteractions(reserveTerminalStep, scheduleDeliveryStep);
    }

    @Test
    void shouldExecuteNextStepWhenResultIsContinue() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        // given
        when(validateCustomerStep.supports(SOLICITADO)).thenReturn(true);
        when(validateCustomerStep.process(request)).thenReturn(WorkflowResult.CONTINUE);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.supports(any())).thenReturn(true);
        when(reserveTerminalStep.process(request)).thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(request);

        // then
        InOrder inOrder = inOrder(validateCustomerStep, reserveTerminalStep, saveGateway);

        inOrder.verify(validateCustomerStep).process(request);
        inOrder.verify(saveGateway).execute(request);
        inOrder.verify(validateCustomerStep).nextStep();
        inOrder.verify(reserveTerminalStep).process(request);
        inOrder.verify(saveGateway).execute(request);

        verify(reserveTerminalStep, never()).nextStep();
        verify(saveGateway, times(2)).execute(request);
    }

    @Test
    void shouldExecuteAllStepsWhileResultIsContinue() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        // given
        when(validateCustomerStep.supports(SOLICITADO)).thenReturn(true);
        when(validateCustomerStep.process(request)).thenReturn(WorkflowResult.CONTINUE);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.supports(any())).thenReturn(true);
        when(reserveTerminalStep.process(request)).thenReturn(WorkflowResult.CONTINUE);
        when(reserveTerminalStep.nextStep()).thenReturn(Optional.of(scheduleDeliveryStep));

        when(scheduleDeliveryStep.supports(any())).thenReturn(true);
        when(scheduleDeliveryStep.process(request)).thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(request);

        // then
        InOrder inOrder = inOrder(validateCustomerStep, reserveTerminalStep, scheduleDeliveryStep, saveGateway);

        inOrder.verify(validateCustomerStep).process(request);
        inOrder.verify(saveGateway).execute(request);
        inOrder.verify(validateCustomerStep).nextStep();

        inOrder.verify(reserveTerminalStep).process(request);
        inOrder.verify(saveGateway).execute(request);
        inOrder.verify(reserveTerminalStep).nextStep();

        inOrder.verify(scheduleDeliveryStep).process(request);
        inOrder.verify(saveGateway).execute(request);

        verify(scheduleDeliveryStep, never()).nextStep();
        verify(saveGateway, times(3)).execute(request);
    }

    @Test
    void shouldStopWhenResultIsContinueButNextStepDoesNotExist() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        // given
        when(validateCustomerStep.supports(SOLICITADO)).thenReturn(true);
        when(validateCustomerStep.process(request)).thenReturn(WorkflowResult.CONTINUE);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.empty());

        // when
        workflow.execute(request);

        // then
        verify(validateCustomerStep).process(request);
        verify(saveGateway).execute(request);
        verify(validateCustomerStep).nextStep();
        verifyNoInteractions(reserveTerminalStep, scheduleDeliveryStep);
    }

    @Test
    void shouldSkipValidateCustomerAndExecuteReserveWhenStatusIsValidado() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        request.validateCustomer();

        // given
        when(validateCustomerStep.supports(VALIDADO)).thenReturn(false);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.supports(VALIDADO)).thenReturn(true);
        when(reserveTerminalStep.process(request)).thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(request);

        // then
        verify(validateCustomerStep, never()).process(request);
        verify(reserveTerminalStep).process(request);
        verify(saveGateway).execute(request);
        verifyNoInteractions(scheduleDeliveryStep);
    }

    @Test
    void shouldSkipValidateAndReserveAndExecuteScheduleWhenStatusIsReservado() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        request.reserveTerminal();

        // given
        when(validateCustomerStep.supports(RESERVADO)).thenReturn(false);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.supports(RESERVADO)).thenReturn(false);
        when(reserveTerminalStep.nextStep()).thenReturn(Optional.of(scheduleDeliveryStep));

        when(scheduleDeliveryStep.supports(RESERVADO)).thenReturn(true);
        when(scheduleDeliveryStep.process(request)).thenReturn(WorkflowResult.STOP);

        // when
        workflow.execute(request);

        // then
        verify(validateCustomerStep, never()).process(request);
        verify(reserveTerminalStep, never()).process(request);
        verify(scheduleDeliveryStep).process(request);
        verify(saveGateway).execute(request);
    }

    @Test
    void shouldNotExecuteAnyStepWhenStatusIsFinal() {
        var request = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        request.scheduleDelivery();

        // given
        when(validateCustomerStep.supports(AGENDADO)).thenReturn(false);
        when(validateCustomerStep.nextStep()).thenReturn(Optional.of(reserveTerminalStep));

        when(reserveTerminalStep.supports(AGENDADO)).thenReturn(false);
        when(reserveTerminalStep.nextStep()).thenReturn(Optional.of(scheduleDeliveryStep));

        when(scheduleDeliveryStep.supports(AGENDADO)).thenReturn(false);
        when(scheduleDeliveryStep.nextStep()).thenReturn(Optional.empty());

        // when
        workflow.execute(request);

        // then
        verify(validateCustomerStep, never()).process(request);
        verify(reserveTerminalStep, never()).process(request);
        verify(scheduleDeliveryStep, never()).process(request);
        verify(saveGateway, never()).execute(request);
    }
}