package com.rede.terminal_api.infrastructure.messaging;

import com.rede.terminal_api.application.event.TerminalRequestCreatedEvent;
import com.rede.terminal_api.application.usecase.ProcessTerminalRequestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TerminalRequestCreatedListener {

    private final ProcessTerminalRequestUseCase processTerminalRequestUseCase;

    @Async
    @EventListener
    public void on(TerminalRequestCreatedEvent event) {
        log.info("Listener executado para terminalRequestId={}", event.terminalRequestId());
        processTerminalRequestUseCase.execute(event.terminalRequestId());
    }

}