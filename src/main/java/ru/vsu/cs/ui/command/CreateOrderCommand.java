package ru.vsu.cs.ui.command;

import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class CreateOrderCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public CreateOrderCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        long clientId = helper.readId("Введите ID клиента:");
        String device = helper.readLine("Введите название устройства:");
        String problemDescription = helper.readLine("Введите описание неисправности:");
        RepairOrder order = service.createOrder(clientId, device, problemDescription);
        System.out.println("Заявка успешно создана. ID заявки: " + order.getId()
                + ", статус - " + order.getStatus());
    }
}
