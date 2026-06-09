package com.rede.terminal_api.presentation.entrypoints.request;

import com.rede.terminal_api.domain.model.Address;

public record AddressRequest(
        String street,
        String number,
        String city,
        String state,
        String zipCode
) {
    public Address toDomain() {
        return new Address(
                street,
                number,
                city,
                state,
                zipCode
        );

    }
}