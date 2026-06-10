package com.rede.terminal_api.infrastructure.http.response;

import com.rede.terminal_api.domain.exception.IntegrationUnavailableException;

public enum IntegrationScenario {
    SUCCESS,
    BUSINESS_FAILURE,
    TECHNICAL_FAILURE;

    public void throwIfIntegrationFailure(String serviceName) {
        if (this == TECHNICAL_FAILURE) {
            throw new IntegrationUnavailableException(
                    serviceName + " unavailable"
            );
        }
    }
}