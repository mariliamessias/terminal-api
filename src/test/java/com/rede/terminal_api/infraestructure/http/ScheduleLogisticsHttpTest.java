package com.rede.terminal_api.infraestructure.http;


import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.infrastructure.http.ScheduleLogisticsHttp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleLogisticsHttpTest {

    @InjectMocks
    private ScheduleLogisticsHttp gateway;

    @Test
    void shouldScheduleDeliveryWhenStateIsSupported() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        // when
        var schedule = gateway.execute(terminalRequest);

        // then
        assertTrue(schedule.isPresent());
        assertNotNull(schedule.get().id());
    }

    @Test
    void shouldScheduleDeliveryWhenStateIsNotMappedAsFailure() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "RJ");

        // when
        var schedule = gateway.execute(terminalRequest);

        // then
        assertTrue(schedule.isPresent());
        assertNotNull(schedule.get().id());
    }

    @Test
    void shouldReturnEmptyWhenLogisticsHasBusinessFailure() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "AM");

        // when
        var schedule = gateway.execute(terminalRequest);

        // then
        assertTrue(schedule.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLogisticsIntegrationFails() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "RR");

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> gateway.execute(terminalRequest)
        );
    }
}