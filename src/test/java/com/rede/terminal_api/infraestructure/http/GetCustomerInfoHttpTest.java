package com.rede.terminal_api.infraestructure.http;

import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;
import com.rede.terminal_api.infrastructure.http.GetCustomerInfoHttp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetCustomerInfoHttpTest {

    @InjectMocks
    private GetCustomerInfoHttp gateway;

    @Test
    void shouldReturnActiveCustomerWhenCustomerIsValid() {
        // given
        var customerId = "CUST-VALID";

        // when
        var customer = gateway.execute(customerId);

        // then
        assertTrue(customer.isPresent());
        assertEquals(customerId, customer.get().id());
        assertTrue(customer.get().active());
    }

    @Test
    void shouldReturnInactiveCustomerWhenCustomerIsInactive() {
        // given
        var customerId = "CUST-INACTIVE";

        // when
        var customer = gateway.execute(customerId);

        // then
        assertTrue(customer.isPresent());
        assertEquals(customerId, customer.get().id());
        assertFalse(customer.get().active());
    }

    @Test
    void shouldReturnEmptyWhenCustomerDoesNotExist() {
        // given
        var customerId = "CUST-NOT-FOUND";

        // when
        var customer = gateway.execute(customerId);

        // then
        assertTrue(customer.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIntegrationFails() {
        // given
        var customerId = "CUST-INTEGRATION-FAIL";

        // when / then
        assertThrows(
                IntegrationUnavailableException.class,
                () -> gateway.execute(customerId)
        );
    }
}