package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SaveTerminalRequestRepository implements SaveTerminalRequestGateway {
    private final TerminalRequestJpaRepository jpaRepository;

    @Override
    public TerminalRequest execute(TerminalRequest request) {
        var entity = TerminalRequestEntity.from(request);
        return jpaRepository.save(entity).toDomain();
    }
}
