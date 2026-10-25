package ru.vsu.cs.ui.command;

import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class UpdateOrderCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public UpdateOrderCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        long orderId = helper.readId("Введите ID заявки:");
        String device = helper.readLine("Введите новое название устройства:");
        String problemDescription = helper.readLine("Введите новое описание неисправности:");
        RepairOrder order = service.updateOrder(orderId, device, problemDescription);
        System.out.println("Заявка обновлена. ID заявки: " + order.getId());
    }
}
