package ru.vsu.cs.service;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.id.IdProvider;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepairOrderServiceTest {
    private final ClientRepository clientRepository = new InMemoryClientRepository();
    private final RepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
    private final RepairOrderService repairOrderService =
            new RepairOrderService(clientRepository, repairOrderRepository, new IdProvider());

    @Test
    void cannotCreateOrderForMissingClient() {
        // given: в репозитории нет клиента с таким ID
        long missingClientId = 999;

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.createOrder(missingClientId, "iphone", "не включается"));

        // then
        assertTrue(repairOrderRepository.findAll().isEmpty());
    }

    @Test
    void cannotCreateOrderWithEmptyDescription() {
        // given
        Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.createOrder(client.getId(), "iphone", ""));

        // then
        assertTrue(repairOrderRepository.findAll().isEmpty());
    }

    @Test
    void createsAndSavesOrderForExistingClient() {
        // given
        Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);

        // when
        RepairOrder order = repairOrderService.createOrder(client.getId(), "iphone", "не запускается");

        // then
        assertEquals(1L, order.getId());
        assertEquals(client.getId(), order.getClient().getId());
        assertEquals("iphone", order.getDevice());
        assertEquals("не запускается", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
        assertEquals(1, repairOrderRepository.findAll().size());
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void canDeleteNewOrder() {
        // given
        RepairOrder order = createOrder();

        // when
        repairOrderService.deleteOrder(order.getId());

        // then
        assertTrue(repairOrderRepository.findById(order.getId()).isEmpty());
    }

    @Test
    void cannotDeleteOrderAfterDiagnosticsStarted() {
        // given
        RepairOrder order = createOrder();
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.deleteOrder(order.getId()));

        // then
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    @Test
    void cannotMoveNewOrderDirectlyToReady() {
        // given
        RepairOrder order = createOrder();

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.READY));

        // then
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void canCompleteRepair() {
        // given
        RepairOrder order = createOrder();

        // when
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.IN_REPAIR);
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.READY);
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.CLOSED);

        // then
        assertEquals(RepairStatus.CLOSED, order.getStatus());
    }

    @Test
    void canCancelNewOrder() {
        // given
        RepairOrder order = createOrder();

        // when
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.CANCELLED);

        // then
        assertEquals(RepairStatus.CANCELLED, order.getStatus());
    }

    @Test
    void canUpdateNewOrder() {
        // given
        RepairOrder order = createOrder();

        // when
        repairOrderService.updateOrder(order.getId(), "samsung", "не работает");

        // then
        assertEquals("samsung", order.getDevice());
        assertEquals("не работает", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void emptyDescriptionDoesNotChangeOrder() {
        // given
        RepairOrder order = createOrder();

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.updateOrder(order.getId(), "samsung", ""));

        // then
        assertEquals("iphone", order.getDevice());
        assertEquals("не включается", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void cannotUpdateOrderAfterDiagnosticsStarted() {
        // given
        RepairOrder order = createOrder();
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);

        // when
        assertThrows(AppException.class,
                () -> repairOrderService.updateOrder(order.getId(), "samsung", "не работает"));

        // then
        assertEquals("iphone", order.getDevice());
        assertEquals("не включается", order.getProblemDescription());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    private RepairOrder createOrder() {
        clientRepository.save(new Client(1, "Stanislav", "89526333468"));
        return repairOrderService.createOrder(1, "iphone", "не включается");
    }
}
