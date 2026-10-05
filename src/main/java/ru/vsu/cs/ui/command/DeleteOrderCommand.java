package ru.vsu.cs.ui.command;

import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class DeleteOrderCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public DeleteOrderCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        long orderId = helper.readId("Введите ID заявки:");
        service.deleteOrder(orderId);
        System.out.println("Заявка удалена. ID заявки: " + orderId);
    }
}
