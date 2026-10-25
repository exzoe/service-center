package ru.vsu.cs.ui.command;

import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class ChangeOrderStatusCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public ChangeOrderStatusCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        long orderId = helper.readId("Введите ID заявки:");
        RepairStatus newStatus = helper.readStatus();
        service.changeOrderStatus(orderId, newStatus);
        System.out.println("Статус заявки изменен. Новый статус - " + newStatus.name());
    }
}
