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

    private TerminalRequest(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address
    ) {
        this.id = id;
        this.customerId = customerId;
        this.terminalType = terminalType;
        this.address = address;
        this.status = SOLICITADO;
        this.createdAt = LocalDateTime.now();
    }

    private TerminalRequest(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address,
            LocalDateTime createdAt,
            TerminalRequestStatus status
    ) {
        this.id = id;
        this.customerId = customerId;
        this.terminalType = terminalType;
        this.address = address;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static TerminalRequest create(
            String customerId,
            TerminalType terminalType,
            Address address
    ) {
        return new TerminalRequest(
                UUID.randomUUID(),
                customerId,
                terminalType,
                address
        );
    }

    public static TerminalRequest restore(
            UUID id,
            String customerId,
            TerminalType terminalType,
            Address address,
            LocalDateTime createdAt,
            TerminalRequestStatus status
    ) {
        return new TerminalRequest(
                id,
                customerId,
                terminalType,
                address,
                createdAt,
                status
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