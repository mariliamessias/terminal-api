package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.domain.gateway.SaveTerminalRequestErrorGateway;
import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestErrorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SaveTerminalRequestErrorRepository implements SaveTerminalRequestErrorGateway {

    private final TerminalRequestErrorJpaRepository jpaRepository;

    @Override
    public void saveOrUpdate(UUID terminalRequestId, String errorMessage) {
        var entity = jpaRepository.findByTerminalRequestId(terminalRequestId)
                .map(existing -> {
                    existing.registerNewAttempt(errorMessage);
                    return existing;
                })
                .orElseGet(() -> TerminalRequestErrorEntity.create(terminalRequestId, errorMessage));

        jpaRepository.save(entity);
    }

    @Override
    public void clear(UUID terminalRequestId) {
        jpaRepository.findByTerminalRequestId(terminalRequestId)
                .ifPresent(entity -> {
                    entity.clearRetry();
                    jpaRepository.save(entity);
                });
    }
}
