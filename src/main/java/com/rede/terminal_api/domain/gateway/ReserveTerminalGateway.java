package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.TerminalReservation;
import com.rede.terminal_api.domain.model.TerminalType;

import java.util.Optional;

public interface ReserveTerminalGateway {
    Optional<TerminalReservation> execute(String customerId, TerminalType terminalType);
}
