package ru.vsu.cs.ui.command;

import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;

public class ShowOrdersCommand implements Command {
    private final RepairOrderService service;
    private final ConsoleHelper helper;

    public ShowOrdersCommand(RepairOrderService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        helper.printOrders(service.findAllOrders());
    }
}
