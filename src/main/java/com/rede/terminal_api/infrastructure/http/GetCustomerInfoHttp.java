package com.rede.terminal_api.infrastructure.http;

import com.rede.terminal_api.domain.gateway.GetCustomerInfoGateway;
import com.rede.terminal_api.domain.model.Customer;
import com.rede.terminal_api.infrastructure.http.response.IntegrationScenario;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class GetCustomerInfoHttp implements GetCustomerInfoGateway {

    private final Map<String, IntegrationScenario> customers = Map.of(
            "CUST-VALID", IntegrationScenario.SUCCESS,
            "CUST-INACTIVE", IntegrationScenario.BUSINESS_FAILURE,
            "CUST-INTEGRATION-FAIL", IntegrationScenario.TECHNICAL_FAILURE
    );

    @Override
    public Optional<Customer> execute(String customerId) {

        return Optional.ofNullable(customers.get(customerId))
                .map(scenario -> {
                    scenario.throwIfIntegrationFailure("Customer service");
                    return new Customer(
                            customerId,
                            scenario == IntegrationScenario.SUCCESS
                    );
                });
    }
}
