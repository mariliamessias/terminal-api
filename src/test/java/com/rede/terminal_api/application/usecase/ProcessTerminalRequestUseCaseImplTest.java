package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.application.usecase.impl.ProcessTerminalRequestUseCaseImpl;
import com.rede.terminal_api.application.workflow.TerminalRequestWorkflow;
import com.rede.terminal_api.domain.exception.TerminalRequestNotFoundException;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTerminalRequestUseCaseImplTest {

    @Mock
    private GetTerminalRequestGateway getTerminalRequestGateway;

    @Mock
    private TerminalRequestWorkflow terminalRequestWorkflow;

    @InjectMocks
    private ProcessTerminalRequestUseCaseImpl useCase;

    @Test
    void shouldProcessTerminalRequestWhenItExists() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(getTerminalRequestGateway.execute(terminalRequest.getId()))
                .thenReturn(Optional.of(terminalRequest));

        // when
        useCase.execute(terminalRequest.getId());

        // then
        verify(getTerminalRequestGateway).execute(terminalRequest.getId());
        verify(terminalRequestWorkflow).execute(terminalRequest);
    }

    @Test
    void shouldThrowExceptionWhenTerminalRequestDoesNotExist() {
        // given
        var terminalRequestId = UUID.randomUUID();

        when(getTerminalRequestGateway.execute(terminalRequestId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                TerminalRequestNotFoundException.class,
                () -> useCase.execute(terminalRequestId)
        );

        verify(getTerminalRequestGateway).execute(terminalRequestId);
        verify(terminalRequestWorkflow, never()).execute(any());

    }
}
