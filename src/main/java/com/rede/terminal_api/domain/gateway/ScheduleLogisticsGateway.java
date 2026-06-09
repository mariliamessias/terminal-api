package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.DeliverySchedule;
import com.rede.terminal_api.domain.model.TerminalRequest;

import java.util.Optional;

public interface ScheduleLogisticsGateway {
    Optional<DeliverySchedule> execute(TerminalRequest request);
}
