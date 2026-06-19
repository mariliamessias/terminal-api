package com.rede.terminal_api.presentation.entrypoint;

import com.rede.terminal_api.domain.gateway.SaveTerminalRequestGateway;
import com.rede.terminal_api.domain.model.TerminalRequest;
import com.rede.terminal_api.domain.model.TerminalType;
import com.rede.terminal_api.infrastructure.repository.TerminalRequestJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.rede.terminal_api.fixture.TerminalRequestFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TerminalRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private SaveTerminalRequestGateway saveTerminalRequestGateway;

    @Autowired
    private TerminalRequestJpaRepository terminalRequestJpaRepository;

    @BeforeEach
    void setUp() {
        reset(saveTerminalRequestGateway);
        terminalRequestJpaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        clearInvocations(saveTerminalRequestGateway);
    }

    @Test
    void shouldCreateTerminalRequest() throws Exception {
        // given
        var payload = validPayload();

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-create-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("CUST-VALID"))
                .andExpect(jsonPath("$.terminalType").value("POS_WIFI"))
                .andExpect(jsonPath("$.status").value("SOLICITADO"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.externalKey").doesNotExist())
                .andExpect(jsonPath("$.version").doesNotExist());
    }

    @Test
    void shouldReturnInternalServerErrorWhenCreateTerminalRequestFailsOnSave() throws Exception {
        // given
        doThrow(new RuntimeException("Error saving terminal request"))
                .when(saveTerminalRequestGateway)
                .execute(any(TerminalRequest.class));

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-create-error")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayload())
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Error saving terminal request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnTerminalRequestWhenItExists() throws Exception {
        // given
        var saved = saveTerminalRequestGateway.execute(
                buildTerminalRequest("CUST-VALID", TerminalType.POS_WIFI, "SP")
        );

        // when / then
        mockMvc.perform(
                        get("/terminal-requests/{id}", saved.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.customerId").value("CUST-VALID"))
                .andExpect(jsonPath("$.terminalType").value("POS_WIFI"))
                .andExpect(jsonPath("$.status").value("SOLICITADO"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.externalKey").doesNotExist())
                .andExpect(jsonPath("$.version").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundWhenTerminalRequestDoesNotExist() throws Exception {
        // given
        var id = UUID.fromString("9552d10a-e05a-4fa6-996d-8de7baf84bfa");

        // when / then
        mockMvc.perform(
                        get("/terminal-requests/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TERMINAL_REQUEST_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Terminal request not found: " + id))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        // given
        var payload = invalidPayload();

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-invalid-payload")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenTerminalTypeIsInvalid() throws Exception {
        // given
        var payload = invalidTerminalTypePayload();

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-invalid-terminal-type")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenTerminalTypeIsBlank() throws Exception {
        // given
        var payload = terminalTypeBlankPayload();

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-terminal-blank")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("terminalType is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenAddressIsNull() throws Exception {
        // given
        var payload = nullAddressPayload();

        // when / then
        mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", "idem-null-address")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("address is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenIdempotencyKeyHeaderIsMissing() throws Exception {
        mockMvc.perform(
                        post("/terminal-requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayload())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Idempotency-Key header is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnSameTerminalRequestWhenIdempotencyKeyIsRetried() throws Exception {
        var idempotencyKey = "idem-retry-1";

        var firstResponse = mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", idempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayload())
                )
                .andExpect(status().isCreated())
                .andReturn();

        var secondResponse = mockMvc.perform(
                        post("/terminal-requests")
                                .header("Idempotency-Key", idempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayload())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        org.assertj.core.api.Assertions.assertThat(terminalRequestJpaRepository.count()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(secondResponse.getResponse().getContentAsString())
                .isEqualTo(firstResponse.getResponse().getContentAsString());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        // given
        var invalidId = "invalid-id";

        // when / then
        mockMvc.perform(
                        get("/terminal-requests/{id}", invalidId)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request parameter: id"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
