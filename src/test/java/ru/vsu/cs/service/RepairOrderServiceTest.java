package ru.vsu.cs.service;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepairOrderServiceTest {
    private final InMemoryClientRepository clientRepository = new InMemoryClientRepository();
    private final InMemoryRepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
    private final RepairOrderService repairOrderService =
            new RepairOrderService(clientRepository, repairOrderRepository);

    @Test
    void cannotCreateOrderForMissingClient() {
        assertEquals(ErrorCode.NOT_FOUND,
                assertThrows(AppException.class,
                        () -> repairOrderService.createOrder(999, "iphone", "не включается")).getCode());
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
    void canDeleteNewOrder() {
        final Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);
        RepairOrder order = repairOrderService.createOrder(1, "iphone", "не запускается");
        repairOrderService.deleteOrder(order.getId());
        assertTrue(repairOrderRepository.findById(order.getId()).isEmpty());
    }

    @Test
    void cannotDeleteOrderAfterDiagnosticsStarted() {
        final Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);
        RepairOrder order = repairOrderService.createOrder(1, "iphone", "не запускается");
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);
        assertEquals(ErrorCode.BUSINESS_RULE,
                assertThrows(AppException.class,
                        () -> repairOrderService.deleteOrder(order.getId())).getCode());
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    private RepairOrder createOrder() {
        clientRepository.save(new Client(1, "Stanislav", "89526333468"));
        return repairOrderService.createOrder(1, "iphone", "не включается");
    }

    @Test
    void cannotMoveNewOrderDirectlyToReady() {
        RepairOrder order = createOrder();
        assertEquals(ErrorCode.BUSINESS_RULE,
                assertThrows(AppException.class,
                        () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.READY)).getCode());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void canCompleteRepair() {
        RepairOrder order = createOrder();
        for (RepairStatus status : new RepairStatus[] {RepairStatus.DIAGNOSTICS,
                RepairStatus.IN_REPAIR, RepairStatus.READY, RepairStatus.CLOSED}) {
            repairOrderService.changeOrderStatus(order.getId(), status);
            assertEquals(status, order.getStatus());
        }
        assertEquals(ErrorCode.BUSINESS_RULE,
                assertThrows(AppException.class,
                        () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW)).getCode());
        assertEquals(RepairStatus.CLOSED, order.getStatus());
    }

    @Test
    void canCancelBeforeRepair() {
        for (boolean startDiagnostics : new boolean[] {false, true}) {
            RepairOrder order = createOrder();
            if (startDiagnostics) {
                repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);
            }
            repairOrderService.changeOrderStatus(order.getId(), RepairStatus.CANCELLED);
            assertEquals(RepairStatus.CANCELLED, order.getStatus());
            assertEquals(ErrorCode.BUSINESS_RULE,
                    assertThrows(AppException.class,
                            () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW)).getCode());
            assertEquals(RepairStatus.CANCELLED, order.getStatus());
        }
    }

    @Test
    void rejectsNullAndRepeatedStatus() {
        RepairOrder order = createOrder();
        assertEquals(ErrorCode.VALIDATION,
                assertThrows(AppException.class,
                        () -> repairOrderService.changeOrderStatus(order.getId(), null)).getCode());
        assertEquals(ErrorCode.BUSINESS_RULE,
                assertThrows(AppException.class,
                        () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW)).getCode());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void canUpdateNewOrderAndInvalidDescriptionDoesNotChangeIt() {
        RepairOrder order = createOrder();
        repairOrderService.updateOrder(order.getId(), "samsung", "не работает");
        assertEquals(ErrorCode.VALIDATION,
                assertThrows(AppException.class,
                        () -> repairOrderService.updateOrder(order.getId(), "другое устройство", " ")).getCode());
        assertEquals("samsung", order.getDevice());
        assertEquals("не работает", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void cannotUpdateOrderAfterDiagnosticsStarted() {
        RepairOrder order = createOrder();
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);
        assertEquals(ErrorCode.BUSINESS_RULE,
                assertThrows(AppException.class,
                        () -> repairOrderService.updateOrder(order.getId(), "samsung", "не работает")).getCode());
        assertEquals("iphone", order.getDevice());
        assertEquals("не включается", order.getProblemDescription());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }
}
