package com.rede.terminal_api.infrastructure.http;

import com.rede.terminal_api.domain.gateway.ReserveTerminalGateway;
import com.rede.terminal_api.domain.model.TerminalReservation;
import com.rede.terminal_api.domain.model.TerminalType;
import com.rede.terminal_api.infrastructure.http.response.IntegrationScenario;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component

public class ReserveTerminalHttp implements ReserveTerminalGateway {

    private final Map<TerminalType, IntegrationScenario> inventory = Map.of(
            TerminalType.POS_WIFI, IntegrationScenario.SUCCESS,
            TerminalType.POS_CHIP, IntegrationScenario.TECHNICAL_FAILURE,
            TerminalType.POS_SMART, IntegrationScenario.BUSINESS_FAILURE
    );

    @Override
    public Optional<TerminalReservation> execute(
            String customerId,
            TerminalType terminalType
    ) {
        return Optional.ofNullable(inventory.get(terminalType))
                .filter(scenario -> {
                    scenario.throwIfIntegrationFailure("Terminal reservation service");
                    return scenario == IntegrationScenario.SUCCESS;
                })
                .map(_ -> new TerminalReservation(UUID.randomUUID().toString()));
    }
}
