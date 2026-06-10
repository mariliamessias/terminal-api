package com.rede.terminal_api.infraestructure.repository;

import com.rede.terminal_api.infrastructure.repository.GetTerminalRequestRepository;
import com.rede.terminal_api.infrastructure.repository.SaveTerminalRequestRepository;
import com.rede.terminal_api.infrastructure.repository.TerminalRequestJpaRepository;
import com.rede.terminal_api.infrastructure.repository.entity.TerminalRequestEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.rede.terminal_api.domain.model.TerminalRequestStatus.SOLICITADO;
import static com.rede.terminal_api.domain.model.TerminalType.POS_WIFI;
import static com.rede.terminal_api.fixture.TerminalRequestFixture.buildTerminalRequest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TerminalRequestRepositoryIntegrationTest {

    @Autowired
    private SaveTerminalRequestRepository saveTerminalRequestRepository;

    @Autowired
    private GetTerminalRequestRepository getTerminalRequestRepository;

    @Autowired
    private TerminalRequestJpaRepository terminalRequestJpaRepository;

    @BeforeEach
    void setUp() {
        terminalRequestJpaRepository.deleteAll();
    }

    @Test
    void shouldPersistDomainTerminalRequestAsEntity() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        // when
        var savedTerminalRequest = saveTerminalRequestRepository.execute(terminalRequest);

        // then
        var entity = terminalRequestJpaRepository.findById(savedTerminalRequest.getId()).orElseThrow();

        assertEquals(terminalRequest.getId(), entity.getId());
        assertEquals("CUST-VALID", entity.getCustomerId());
        assertEquals(POS_WIFI, entity.getTerminalType());
        assertEquals("Rua Exemplo", entity.getStreet());
        assertEquals("100", entity.getNumber());
        assertEquals("São Paulo", entity.getCity());
        assertEquals("SP", entity.getState());
        assertEquals("01000-000", entity.getZipCode());
        assertEquals(SOLICITADO, entity.getStatus());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void shouldRestoreDomainTerminalRequestFromPersistedEntity() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        terminalRequestJpaRepository.save(
                TerminalRequestEntity.from(terminalRequest)
        );

        // when
        var result = getTerminalRequestRepository.execute(terminalRequest.getId());

        // then
        assertTrue(result.isPresent());
        assertEquals(terminalRequest.getId(), result.get().getId());
        assertEquals("CUST-VALID", result.get().getCustomerId());
        assertEquals(POS_WIFI, result.get().getTerminalType());
        assertEquals(SOLICITADO, result.get().getStatus());
        assertEquals(terminalRequest.getAddress(), result.get().getAddress());
    }

    @Test
    void shouldReturnEmptyWhenTerminalRequestDoesNotExist() {
        // given
        var terminalRequest = buildTerminalRequest("CUST-VALID", POS_WIFI, "SP");

        // when
        var result = getTerminalRequestRepository.execute(terminalRequest.getId());

        // then
        assertTrue(result.isEmpty());
    }
}