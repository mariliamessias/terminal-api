package com.rede.terminal_api.domain.gateway;

import com.rede.terminal_api.domain.model.Customer;

import java.util.Optional;

public interface GetCustomerInfoGateway {
    Optional<Customer> execute(String customerId);
}
