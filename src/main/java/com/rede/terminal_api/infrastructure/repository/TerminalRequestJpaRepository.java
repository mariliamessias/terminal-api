package com.rede.terminal_api.infrastructure.repository;

import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TerminalRequestJpaRepository extends JpaRepository<TerminalRequestEntity, UUID> {
    Optional<TerminalRequestEntity> findByExternalKey(String externalKey);
}
