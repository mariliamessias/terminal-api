package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.application.event.TerminalRequestCreatedEvent;
import com.rede.terminal_api.application.usecase.impl.CreateTerminalRequestUseCaseImpl;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTerminalRequestUseCaseImplTest {

    @Mock
    private SaveTerminalRequestGateway saveGateway;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<TerminalRequestCreatedEvent> eventCaptor;

    @InjectMocks
    private CreateTerminalRequestUseCaseImpl useCase;

    @Test
    void shouldSaveTerminalRequestAndPublishCreatedEvent() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(saveGateway.execute(terminalRequest))
                .thenReturn(terminalRequest);

        // when
        var result = useCase.execute(terminalRequest);

        // then
        assertEquals(terminalRequest, result);

        verify(saveGateway).execute(terminalRequest);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertEquals(
                terminalRequest.getId(),
                eventCaptor.getValue().terminalRequestId()
        );
    }

    @Test
    void shouldThrowExceptionAndNotPublishEventWhenSaveFails() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");
        var exception = new RuntimeException("Error saving terminal request");

        when(saveGateway.execute(terminalRequest))
                .thenThrow(exception);

        // when / then
        var result = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(terminalRequest)
        );

        assertEquals("Error saving terminal request", result.getMessage());

        verify(saveGateway).execute(terminalRequest);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
