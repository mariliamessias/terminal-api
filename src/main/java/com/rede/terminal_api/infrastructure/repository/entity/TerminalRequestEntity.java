package com.rede.terminal_api.infrastructure.repository.entity;

import com.rede.terminal_api.domain.model.Address;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalRequestStatus;
import com.rede.terminal_api.domain.model.TerminalType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "terminal_requests",
        uniqueConstraints = @UniqueConstraint(name = "uk_terminal_requests_external_key", columnNames = "external_key")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TerminalRequestEntity {

    @Id
    private UUID id;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private TerminalType terminalType;

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;

    @Column(name = "external_key")
    private String externalKey;

    @Enumerated(EnumType.STRING)
    private TerminalRequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public TerminalRequestEntity(
            UUID id,
            String customerId,
            TerminalType terminalType,
            String street,
            String number,
            String city,
            String state,
            String zipCode,
            String externalKey,
            TerminalRequestStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.terminalType = terminalType;
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.externalKey = externalKey;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static TerminalRequestEntity from(TerminalRequest request) {
        var address = request.getAddress();

        return new TerminalRequestEntity(
                request.getId(),
                request.getCustomerId(),
                request.getTerminalType(),
                address.street(),
                address.number(),
                address.city(),
                address.state(),
                address.zipCode(),
                request.getExternalKey(),
                request.getStatus(),
                request.getCreatedAt(),
                LocalDateTime.now(),
                request.getVersion()
        );
    }

    public TerminalRequest toDomain() {
        return TerminalRequest.restore(
                id,
                customerId,
                terminalType,
                new Address(
                        street,
                        number,
                        city,
                        state,
                        zipCode
                ),
                createdAt,
                status,
                externalKey,
                version
        );
    }
}
