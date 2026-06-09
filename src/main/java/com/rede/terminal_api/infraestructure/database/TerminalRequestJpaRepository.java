package com.rede.terminal_api.infraestructure.database;

import com.rede.terminal_api.infraestructure.database.entities.TerminalRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TerminalRequestJpaRepository extends JpaRepository<TerminalRequestEntity, UUID> {
}
