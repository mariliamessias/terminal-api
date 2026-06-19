package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.application.usecase.impl.ProcessTerminalRequestUseCaseImpl;
import com.rede.terminal_api.application.workflow.TerminalRequestWorkflow;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestByIdGateway;
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
    }
}
