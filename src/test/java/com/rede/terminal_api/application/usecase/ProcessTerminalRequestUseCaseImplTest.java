package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.application.usecase.impl.ProcessTerminalRequestUseCaseImpl;
import com.rede.terminal_api.application.workflow.TerminalRequestWorkflow;
import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestByIdGateway;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestErrorGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTerminalRequestUseCaseImplTest {

    @Mock
    private GetTerminalRequestByIdGateway getTerminalRequestByIdGateway;

    @Mock
    private SaveTerminalRequestErrorGateway saveTerminalRequestErrorGateway;

    @Mock
    private TerminalRequestWorkflow terminalRequestWorkflow;

    @InjectMocks
    private ProcessTerminalRequestUseCaseImpl useCase;

    @Test
    void shouldProcessTerminalRequestWhenItExists() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        var eventId = UUID.randomUUID();

        when(getTerminalRequestByIdGateway.execute(terminalRequest.getId()))
                .thenReturn(Optional.of(terminalRequest));

        // when
        useCase.execute(eventId, terminalRequest.getId());

        // then
        verify(getTerminalRequestByIdGateway).execute(terminalRequest.getId());
        verify(terminalRequestWorkflow).execute(terminalRequest);
        verify(saveTerminalRequestErrorGateway, never()).clear(any());
        verify(saveTerminalRequestErrorGateway, never()).saveOrUpdate(any(), any());
    }

    @Test
    void shouldIgnoreWhenTerminalRequestDoesNotExist() {
        // given
        var eventId = UUID.randomUUID();
        var terminalRequestId = UUID.randomUUID();

        when(getTerminalRequestByIdGateway.execute(terminalRequestId))
                .thenReturn(Optional.empty());

        // when
        useCase.execute(eventId, terminalRequestId);

        verify(getTerminalRequestByIdGateway).execute(terminalRequestId);
        verify(terminalRequestWorkflow, never()).execute(any());
        verify(saveTerminalRequestErrorGateway, never()).saveOrUpdate(any(), any());
        verify(saveTerminalRequestErrorGateway, never()).clear(any());
    }

    @Test
    void shouldSaveTechnicalFailureWhenIntegrationIsUnavailable() {
        var eventId = UUID.randomUUID();
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(getTerminalRequestByIdGateway.execute(terminalRequest.getId()))
                .thenReturn(Optional.of(terminalRequest));

        doThrow(new IntegrationUnavailableException("Customer service unavailable"))
                .when(terminalRequestWorkflow).execute(terminalRequest);

        useCase.execute(eventId, terminalRequest.getId());

        verify(getTerminalRequestByIdGateway).execute(terminalRequest.getId());
        verify(terminalRequestWorkflow).execute(terminalRequest);
        verify(saveTerminalRequestErrorGateway)
                .saveOrUpdate(terminalRequest.getId(), "Customer service unavailable");
        verify(saveTerminalRequestErrorGateway, never()).clear(any());
    }
}
