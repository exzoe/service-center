package ru.vsu.cs.service;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.id.IdProvider;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientServiceTest {
    private final ClientRepository clientRepository = new InMemoryClientRepository();
    private final ClientService clientService = new ClientService(clientRepository, new IdProvider());

    @Test
    void createsClientsWithDistinctIds() {
        // given
        Client firstClient = clientService.createClient("Stanislav", "89526333468");

        // when
        Client secondClient = clientService.createClient("Ivan", "89991234567");

        // then
        assertEquals(1L, firstClient.getId());
        assertNotEquals(firstClient.getId(), secondClient.getId());
        assertEquals(2, clientRepository.findAll().size());
        assertTrue(clientRepository.findById(firstClient.getId()).isPresent());
        assertTrue(clientRepository.findById(secondClient.getId()).isPresent());
    }
}
