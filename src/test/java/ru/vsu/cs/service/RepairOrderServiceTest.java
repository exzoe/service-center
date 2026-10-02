package ru.vsu.cs.service;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;

import static org.junit.jupiter.api.Assertions.*;

public class RepairOrderServiceTest {
    private final InMemoryClientRepository clientRepository = new InMemoryClientRepository();
    private final InMemoryRepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
    private final RepairOrderService repairOrderService = new RepairOrderService(clientRepository, repairOrderRepository);

    @Test
    void cannotCreateOrderForMissingClient() {
        assertThrows(IllegalArgumentException.class, () -> repairOrderService.createOrder(999,
                "iphone", "не включается"));
        assertTrue(repairOrderRepository.findAll().isEmpty());
    }

    @Test
    void createsAndSavesOrderForExistingClient() {
        final Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);
        RepairOrder order = repairOrderService.createOrder(1, "iphone", "не запускается");
        assertEquals(1L, order.getClient().getId());
        assertEquals(RepairStatus.NEW, order.getStatus());
        assertEquals(1, repairOrderRepository.findAll().size());
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void canDeleteNewOrder(){
        final Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);
        RepairOrder order = repairOrderService.createOrder(1, "iphone", "не запускается");
        repairOrderService.deleteOrder(order.getId());
        assertTrue(repairOrderRepository.findById(order.getId()).isEmpty());
    }

    @Test
    void cannotDeleteOrderAfterDiagnosticsStarted(){
        final Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);
        RepairOrder order = repairOrderService.createOrder(1, "iphone", "не запускается");
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);
        assertThrows(IllegalStateException.class, () -> repairOrderService.deleteOrder(order.getId()));
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }
}
