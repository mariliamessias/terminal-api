package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TerminalRequestErrorJpaRepository extends JpaRepository<TerminalRequestErrorEntity, UUID> {
    Optional<TerminalRequestErrorEntity> findByTerminalRequestId(UUID terminalRequestId);
}
