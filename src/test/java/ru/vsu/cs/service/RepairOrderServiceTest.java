package ru.vsu.cs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;
import ru.vsu.cs.id.IdProvider;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.createOrder(missingClientId, "iphone", "не включается"));

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getCode());
        assertTrue(repairOrderRepository.findAll().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidOrderDetails")
    void cannotCreateOrderWithInvalidDetails(String device, String problemDescription) {
        // given
        Client client = new Client(1, "Stanislav", "89526333468");
        clientRepository.save(client);

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.createOrder(client.getId(), device, problemDescription));

        // then
        assertEquals(ErrorCode.VALIDATION, exception.getCode());
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
    void createsOrdersWithDistinctIds() {
        // given
        RepairOrder firstOrder = createOrder();

        // when
        RepairOrder secondOrder = repairOrderService.createOrder(
                firstOrder.getClient().getId(), "samsung", "не заряжается");

        // then
        assertNotEquals(firstOrder.getId(), secondOrder.getId());
        assertEquals(2, repairOrderRepository.findAll().size());
        assertTrue(repairOrderRepository.findById(firstOrder.getId()).isPresent());
        assertTrue(repairOrderRepository.findById(secondOrder.getId()).isPresent());
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
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.deleteOrder(order.getId()));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
        assertTrue(repairOrderRepository.findById(order.getId()).isPresent());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    @Test
    void cannotMoveNewOrderDirectlyToReady() {
        // given
        RepairOrder order = createOrder();

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.READY));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void canCompleteRepair() {
        // given
        RepairOrder order = createOrder();

        // when
        completeRepair(order.getId());

        // then
        assertEquals(RepairStatus.CLOSED, order.getStatus());
        assertEquals(RepairStatus.CLOSED,
                repairOrderRepository.findById(order.getId()).orElseThrow().getStatus());
    }

    @Test
    void cannotChangeClosedOrderStatus() {
        // given
        RepairOrder order = createOrder();
        completeRepair(order.getId());

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
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
    void canCancelOrderAfterDiagnosticsStarted() {
        // given
        RepairOrder order = createOrder();
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.DIAGNOSTICS);

        // when
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.CANCELLED);

        // then
        assertEquals(RepairStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cannotChangeCancelledOrderStatus() {
        // given
        RepairOrder order = createOrder();
        repairOrderService.changeOrderStatus(order.getId(), RepairStatus.CANCELLED);

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
        assertEquals(RepairStatus.CANCELLED, order.getStatus());
    }

    @Test
    void rejectsNullStatus() {
        // given
        RepairOrder order = createOrder();

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), null));

        // then
        assertEquals(ErrorCode.VALIDATION, exception.getCode());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void rejectsRepeatedStatus() {
        // given
        RepairOrder order = createOrder();

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.changeOrderStatus(order.getId(), RepairStatus.NEW));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
        assertEquals(RepairStatus.NEW, order.getStatus());
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

    @ParameterizedTest
    @MethodSource("invalidOrderDetails")
    void invalidDetailsDoNotChangeOrder(String device, String problemDescription) {
        // given
        RepairOrder order = createOrder();

        // when
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.updateOrder(order.getId(), device, problemDescription));

        // then
        assertEquals(ErrorCode.VALIDATION, exception.getCode());
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
        AppException exception = assertThrows(AppException.class,
                () -> repairOrderService.updateOrder(order.getId(), "samsung", "не работает"));

        // then
        assertEquals(ErrorCode.BUSINESS_RULE, exception.getCode());
        assertEquals("iphone", order.getDevice());
        assertEquals("не включается", order.getProblemDescription());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    private static Stream<Arguments> invalidOrderDetails() {
        return Stream.of(
                Arguments.of(null, "другое описание"),
                Arguments.of("", "другое описание"),
                Arguments.of(" ", "другое описание"),
                Arguments.of("другое устройство", null),
                Arguments.of("другое устройство", ""),
                Arguments.of("другое устройство", " ")
        );
    }

    private RepairOrder createOrder() {
        clientRepository.save(new Client(1, "Stanislav", "89526333468"));
        return repairOrderService.createOrder(1, "iphone", "не включается");
    }

    private void completeRepair(long orderId) {
        repairOrderService.changeOrderStatus(orderId, RepairStatus.DIAGNOSTICS);
        repairOrderService.changeOrderStatus(orderId, RepairStatus.IN_REPAIR);
        repairOrderService.changeOrderStatus(orderId, RepairStatus.READY);
        repairOrderService.changeOrderStatus(orderId, RepairStatus.CLOSED);
    }
}
