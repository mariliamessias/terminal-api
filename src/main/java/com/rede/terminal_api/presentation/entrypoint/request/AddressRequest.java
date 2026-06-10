package com.rede.terminal_api.presentation.entrypoint.request;

import com.rede.terminal_api.domain.model.Address;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

        @NotBlank(message = "street is required")
        String street,

        @NotBlank(message = "number is required")
        String number,

        @NotBlank(message = "city is required")
        String city,

        @NotBlank(message = "state is required")
        String state,

        @NotBlank(message = "zipCode is required")
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