package com.rede.terminal_api.fixture;

import com.rede.terminal_api.domain.model.Address;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TerminalRequestFixture {

    public static TerminalRequest buildTerminalRequest(
            String customerId,
            TerminalType terminalType,
            String state
    ) {
        return TerminalRequest.create(
                customerId,
                terminalType,
                buildAddress(state)
        );
    }

    public static Address buildAddress(String state) {
        return new Address(
                "Rua Exemplo",
                "100",
                cityByState(state),
                state,
                zipCodeByState(state)
        );
    }

    public static String validPayload() {
        return """
                {
                  "customerId": "CUST-VALID",
                  "terminalType": "POS_WIFI",
                  "address": {
                    "street": "Rua Exemplo",
                    "number": "100",
                    "city": "São Paulo",
                    "state": "SP",
                    "zipCode": "01000-000"
                  }
                }
                """;
    }

    public static String invalidPayload() {
        return """
                {
                  "customerId": "",
                  "terminalType": "POS_WIFI",
                  "address": {
                    "street": "",
                    "number": "100",
                    "city": "São Paulo",
                    "state": "SP",
                    "zipCode": "01000-000"
                  }
                }
                """;
    }

    public static String invalidTerminalTypePayload() {
        return """
                {
                  "customerId": "CUST-VALID",
                  "terminalType": "POS_INVALID",
                  "address": {
                    "street": "Rua Exemplo",
                    "number": "100",
                    "city": "São Paulo",
                    "state": "SP",
                    "zipCode": "01000-000"
                  }
                }
                """;
    }

    public static String terminalTypeBlankPayload() {
        return """
                {
                  "customerId": "CUST-VALID",
                  "terminalType": "",
                  "address": {
                    "street": "Rua Exemplo",
                    "number": "100",
                    "city": "São Paulo",
                    "state": "SP",
                    "zipCode": "01000-000"
                  }
                }
                """;
    }

    public static String nullAddressPayload() {
        return """
                {
                  "customerId": "CUST-VALID",
                  "terminalType": "POS_WIFI",
                  "address": null
                }
                """;
    }

    private static String cityByState(String state) {
        return switch (state) {
            case "AM" -> "Manaus";
            case "RR" -> "Boa Vista";
            default -> "São Paulo";
        };
    }

    private static String zipCodeByState(String state) {
        return switch (state) {
            case "AM" -> "69000-000";
            case "RR" -> "69300-000";
            default -> "01000-000";
        };
    }
}
