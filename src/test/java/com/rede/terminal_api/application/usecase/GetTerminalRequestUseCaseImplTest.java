package com.rede.terminal_api.application.usecase;

import com.rede.terminal_api.application.usecase.impl.GetTerminalRequestUseCaseImpl;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTerminalRequestUseCaseImplTest {

    @Mock
    private GetTerminalRequestGateway gateway;

    @InjectMocks
    private GetTerminalRequestUseCaseImpl useCase;

    @Test
    void shouldReturnTerminalRequestWhenItExists() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        when(gateway.execute(terminalRequest.getId()))
                .thenReturn(Optional.of(terminalRequest));

        // when
        var result = useCase.execute(terminalRequest.getId());

        // then
        assertEquals(terminalRequest, result);

        verify(gateway).execute(terminalRequest.getId());
    }

    @Test
    void shouldThrowExceptionWhenTerminalRequestDoesNotExist() {
        // given
        var terminalRequestId = UUID.randomUUID();

        when(gateway.execute(terminalRequestId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                TerminalRequestNotFoundException.class,
                () -> useCase.execute(terminalRequestId)
        );

        verify(gateway).execute(terminalRequestId);
    }
}