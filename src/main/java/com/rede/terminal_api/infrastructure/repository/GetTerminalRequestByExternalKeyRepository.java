package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.domain.gateway.GetTerminalRequestByExternalKeyGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetTerminalRequestByExternalKeyRepository implements GetTerminalRequestByExternalKeyGateway {

    private final TerminalRequestJpaRepository jpaRepository;

    @Override
    public Optional<TerminalRequest> execute(String externalKey) {
        return jpaRepository.findByExternalKey(externalKey)
                .map(TerminalRequestEntity::toDomain);
    }
}
