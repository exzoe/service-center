package ru.vsu.cs.domain;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepairOrderTest {
    private final Client client = new Client(1, "Stanislav", "89526333468");
    private final RepairOrder order = new RepairOrder(1, client, "iphone", "не включается");

    @Test
    void canUpdateNewOrderDetails() {
        order.updateDetails("samsung", "не работает");
        assertEquals("samsung", order.getDevice());
        assertEquals("не работает", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void invalidDescriptionDoesNotChangeOrderDetails() {
        String originalDevice = order.getDevice();
        String originalDescription = order.getProblemDescription();
        assertEquals(ErrorCode.VALIDATION,
                assertThrows(AppException.class,
                        () -> order.updateDetails("samsung", " ")).getCode());
        assertEquals(originalDevice, order.getDevice());
        assertEquals(originalDescription, order.getProblemDescription());
    }
}
