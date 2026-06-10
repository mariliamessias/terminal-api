package com.rede.terminal_api.infraestructure.http;


import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.infrastructure.http.ReserveTerminalHttp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rede.terminal_api.domain.model.TerminalType.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReserveTerminalHttpTest {

    @InjectMocks
    private ReserveTerminalHttp gateway;

    @Test
    void shouldReserveTerminalWhenTerminalIsAvailable() {
        // given
        var customerId = "CUST-VALID";

        // when
        var reservation = gateway.execute(customerId, POS_WIFI);

        // then
        assertTrue(reservation.isPresent());
        assertNotNull(reservation.get().id());
    }

    @Test
    void shouldReturnEmptyWhenTerminalIsUnavailable() {
        // given
        var customerId = "CUST-VALID";

        // when
        var reservation = gateway.execute(customerId, POS_SMART);

        // then
        assertTrue(reservation.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenReservationIntegrationFails() {
        // given
        var customerId = "CUST-VALID";

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> gateway.execute(customerId, POS_CHIP)
        );
    }
}