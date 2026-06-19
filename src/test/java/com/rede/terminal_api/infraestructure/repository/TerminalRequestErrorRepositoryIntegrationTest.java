package com.rede.terminal_api.infraestructure.repository;

import com.rede.terminal_api.infrastructure.repository.SaveTerminalRequestErrorRepository;
import com.rede.terminal_api.infrastructure.repository.SaveTerminalRequestRepository;
import com.rede.terminal_api.infrastructure.repository.TerminalRequestErrorJpaRepository;
import com.rede.terminal_api.infrastructure.repository.TerminalRequestJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TerminalRequestErrorRepositoryIntegrationTest {

    @Autowired
    private SaveTerminalRequestRepository saveTerminalRequestRepository;

    @Autowired
    private SaveTerminalRequestErrorRepository saveTerminalRequestErrorRepository;

    @Autowired
    private TerminalRequestErrorJpaRepository terminalRequestErrorJpaRepository;

    @Autowired
    private TerminalRequestJpaRepository terminalRequestJpaRepository;

    @BeforeEach
    void setUp() {
        terminalRequestErrorJpaRepository.deleteAll();
        terminalRequestJpaRepository.deleteAll();
    }

    @Test
    void shouldCreateErrorForTerminalRequest() {
        var terminalRequest = saveTerminalRequestRepository.execute(
                buildTerminalRequest("CUST-VALID", POS_WIFI, "SP", "external-key-error-1")
        );

        saveTerminalRequestErrorRepository.saveOrUpdate(terminalRequest.getId(), "Integration unavailable");

        var savedError = terminalRequestErrorJpaRepository.findByTerminalRequestId(terminalRequest.getId()).orElseThrow();

        assertNotNull(savedError.getId());
        assertEquals(terminalRequest.getId(), savedError.getTerminalRequestId());
        assertEquals("Integration unavailable", savedError.getLastError());
        assertEquals(1, savedError.getRetryCount());
        assertTrue(savedError.isRetryPending());
        assertNotNull(savedError.getLastAttemptAt());
    }

    @Test
    void shouldUpdateExistingErrorForSameTerminalRequest() {
        var terminalRequest = saveTerminalRequestRepository.execute(
                buildTerminalRequest("CUST-VALID", POS_WIFI, "SP", "external-key-error-2")
        );

        saveTerminalRequestErrorRepository.saveOrUpdate(terminalRequest.getId(), "First error");
        saveTerminalRequestErrorRepository.saveOrUpdate(terminalRequest.getId(), "Second error");

        var savedError = terminalRequestErrorJpaRepository.findByTerminalRequestId(terminalRequest.getId()).orElseThrow();

        assertEquals("Second error", savedError.getLastError());
        assertEquals(2, savedError.getRetryCount());
        assertTrue(savedError.isRetryPending());
        assertEquals(1, terminalRequestErrorJpaRepository.count());
    }
}
