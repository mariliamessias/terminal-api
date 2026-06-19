package com.rede.terminal_api.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.rede.terminal_api.domain.model.TerminalRequestStatus.*;

@Getter
public class TerminalRequest {

    private final UUID id;
    private final String customerId;
    private final TerminalType terminalType;
    private final Address address;
    private final LocalDateTime createdAt;
    private TerminalRequestStatus status;
    private final String externalKey;
    private final Long version;

    private TerminalRequest(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address,
            String externalKey,
            Long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.terminalType = terminalType;
        this.address = address;
        this.status = SOLICITADO;
        this.createdAt = LocalDateTime.now();
        this.externalKey = externalKey;
        this.version = version;
    }

    private TerminalRequest(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address,
            LocalDateTime createdAt,
            TerminalRequestStatus status,
            String externalKey,
            Long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.terminalType = terminalType;
        this.address = address;
        this.createdAt = createdAt;
        this.status = status;
        this.externalKey = externalKey;
        this.version = version;
    }

    public static TerminalRequest create(
            String customerId,
            TerminalType terminalType,
            Address address,
            String externalKey
    ) {
        return new TerminalRequest(
                UUID.randomUUID(),
                customerId,
                terminalType,
                address,
                externalKey,
                null
        );
    }

    public static TerminalRequest restore(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address,
            LocalDateTime createdAt,
            TerminalRequestStatus status,
            String externalKey,
            Long version
    ) {
        return new TerminalRequest(
                id,
                customerId,
                terminalType,
                address,
                createdAt,
                status,
                externalKey,
                version
        );
    }

    public void validateCustomer() {
        this.status = VALIDADO;
    }

    public void rejectCustomer() {
        this.status = REJEITADO;
    }

    public void reserveTerminal() {
        this.status = RESERVADO;
    }

    public void failReservation() {
        this.status = ERRO_RESERVA;
    }

    public void scheduleDelivery() {
        this.status = AGENDADO;
    }

    public void failScheduling() {
        this.status = ERRO_AGENDAMENTO;
    }
}
