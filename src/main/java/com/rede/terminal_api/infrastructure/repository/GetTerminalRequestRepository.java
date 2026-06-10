package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.domain.gateway.GetTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GetTerminalRequestRepository implements GetTerminalRequestGateway {

    private final TerminalRequestJpaRepository jpaRepository;

    @Override
    public Optional<TerminalRequest> execute(UUID id) {
        return jpaRepository.findById(id).map(TerminalRequestEntity::toDomain);
    }
}
