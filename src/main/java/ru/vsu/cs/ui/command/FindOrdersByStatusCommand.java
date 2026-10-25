package ru.vsu.cs.ui.command;

import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class FindOrdersByStatusCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public FindOrdersByStatusCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        RepairStatus status = helper.readStatus();
        helper.printOrders(service.findOrdersByStatus(status));
    }
}
