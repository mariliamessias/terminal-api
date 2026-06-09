package com.rede.terminal_api.domain.model;

public record Address(
        String street,
        String number,
        String city,
        String state,
        String zipCode
) {

}
