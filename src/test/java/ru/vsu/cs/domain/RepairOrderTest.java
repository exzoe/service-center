package ru.vsu.cs.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepairOrderTest {

    private final Client client = new Client(1, "Stanislav", "89526333468");
    private final RepairOrder order = new RepairOrder(1, client, "iphone", "не включается");

    @Test
    void cannotMoveNewOrderDirectlyToReady() {

        assertThrows(IllegalStateException.class, () -> order.changeStatus(RepairStatus.READY));
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void canMoveNewOrderToDiagnostics() {
        order.changeStatus(RepairStatus.DIAGNOSTICS);
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

    @Test
    void canUpdateNewOrderDetails() {
        order.updateDetails("samsung", "не работает");
        assertEquals("samsung", order.getDevice());
        assertEquals("не работает", order.getProblemDescription());
        assertEquals(RepairStatus.NEW, order.getStatus());
    }

    @Test
    void invalidDescriptionDoesNotChangeOrderDetails(){
        String originalDevice = order.getDevice();
        String originalDescription = order.getProblemDescription();
        assertThrows(IllegalArgumentException.class, () -> order.updateDetails("samsung", " "));
        assertEquals(originalDevice, order.getDevice());
        assertEquals(originalDescription, order.getProblemDescription());
    }

    @Test
    void cannotUpdateOrderAfterDiagnosticsStarted(){
        order.changeStatus(RepairStatus.DIAGNOSTICS);
        assertThrows(IllegalStateException.class, () -> order.updateDetails("samsung", "не работает"));
        assertEquals("iphone", order.getDevice());
        assertEquals("не включается", order.getProblemDescription());
        assertEquals(RepairStatus.DIAGNOSTICS, order.getStatus());
    }

}
