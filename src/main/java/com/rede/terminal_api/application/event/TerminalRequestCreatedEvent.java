package com.rede.terminal_api.application.event;

import java.util.UUID;

public record TerminalRequestCreatedEvent(
        UUID terminalRequestId
) {
}