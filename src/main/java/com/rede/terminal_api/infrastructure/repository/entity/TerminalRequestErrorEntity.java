package com.rede.terminal_api.infrastructure.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "terminal_request_errors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TerminalRequestErrorEntity {

    @Id
    private UUID id;

    @Column(name = "terminal_request_id", nullable = false, unique = true)
    private UUID terminalRequestId;

    private String lastError;

    private Integer retryCount;

    private LocalDateTime lastAttemptAt;

    private boolean retryPending;

    public TerminalRequestErrorEntity(
            UUID id,
            UUID terminalRequestId,
            String lastError,
            Integer retryCount,
            LocalDateTime lastAttemptAt,
            boolean retryPending
    ) {
        this.id = id;
        this.terminalRequestId = terminalRequestId;
        this.lastError = lastError;
        this.retryCount = retryCount;
        this.lastAttemptAt = lastAttemptAt;
        this.retryPending = retryPending;
    }

    public static TerminalRequestErrorEntity create(UUID terminalRequestId, String errorMessage) {
        return new TerminalRequestErrorEntity(
                UUID.randomUUID(),
                terminalRequestId,
                errorMessage,
                1,
                LocalDateTime.now(),
                true
        );
    }

    public void registerNewAttempt(String errorMessage) {
        this.lastError = errorMessage;
        this.retryCount = retryCount + 1;
        this.lastAttemptAt = LocalDateTime.now();
        this.retryPending = true;
    }

    public void clearRetry() {
        this.retryPending = false;
        this.lastAttemptAt = LocalDateTime.now();
    }
}
