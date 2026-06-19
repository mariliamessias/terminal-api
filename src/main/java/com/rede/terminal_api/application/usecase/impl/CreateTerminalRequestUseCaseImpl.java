package com.rede.terminal_api.application.usecase.impl;

import com.rede.terminal_api.application.event.TerminalRequestCreatedEvent;
import com.rede.terminal_api.application.usecase.CreateTerminalRequestUseCase;
import com.rede.terminal_api.domain.gateway.GetTerminalRequestByExternalKeyGateway;
import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CreateTerminalRequestUseCaseImpl implements CreateTerminalRequestUseCase {

    private final SaveTerminalRequestGateway saveTerminalRequestGateway;
    private final GetTerminalRequestByExternalKeyGateway getTerminalRequestByExternalKeyGateway;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TerminalRequest execute(TerminalRequest request) {
        var existingRequest = getTerminalRequestByExternalKeyGateway.execute(request.getExternalKey());

        if (existingRequest.isPresent()) {
            log.info("Returning existing terminal request for externalKey={}", request.getExternalKey());
            return existingRequest.get();
        }

        val saved = saveTerminalRequestGateway.execute(request);
        log.info("Publishing TerminalRequestCreatedEvent. id={}", saved.getId());
        eventPublisher.publishEvent(
                new TerminalRequestCreatedEvent(UUID.randomUUID(), saved.getId())
        );
        return saved;
    }
}
