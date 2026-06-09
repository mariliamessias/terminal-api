package com.rede.terminal_api.infraestructure.http;

import com.rede.terminal_api.domain.gateway.ScheduleLogisticsGateway;
import com.rede.terminal_api.domain.model.DeliverySchedule;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.infraestructure.http.response.IntegrationScenario;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ScheduleLogisticsHttp implements ScheduleLogisticsGateway {

    private final Map<String, IntegrationScenario> logisticsByState = Map.of(
            "AM", IntegrationScenario.BUSINESS_FAILURE,
            "RR", IntegrationScenario.TECHNICAL_FAILURE
    );

    @Override
    public Optional<DeliverySchedule> execute(TerminalRequest request) {
        return Optional.of(logisticsByState.getOrDefault(
                        request.getAddress()
                                .state()
                                .toUpperCase(),
                        IntegrationScenario.SUCCESS
                ))
                .filter(scenario -> {
                    scenario.throwIfIntegrationFailure("Logistics scheduling service");
                    return scenario == IntegrationScenario.SUCCESS;
                })
                .map(_ -> new DeliverySchedule(UUID.randomUUID().toString()));
    }
}
