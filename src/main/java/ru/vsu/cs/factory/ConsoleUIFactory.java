package ru.vsu.cs.factory;

import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleHelper;
import ru.vsu.cs.ui.ConsoleUI;
import ru.vsu.cs.ui.command.AddClientCommand;
import ru.vsu.cs.ui.command.ChangeOrderStatusCommand;
import ru.vsu.cs.ui.command.CreateOrderCommand;
import ru.vsu.cs.ui.command.DeleteOrderCommand;
import ru.vsu.cs.ui.command.FindOrdersByStatusCommand;
import ru.vsu.cs.ui.command.ShowClientsCommand;
import ru.vsu.cs.ui.command.ShowOrdersCommand;
import ru.vsu.cs.ui.command.UpdateOrderCommand;

import java.util.Scanner;

public final class ConsoleUIFactory {
    private ConsoleUIFactory() {
    }

    public static ConsoleUI create(ClientService clientService, RepairOrderService repairOrderService) {
        ConsoleHelper helper = new ConsoleHelper(new Scanner(System.in));
        ConsoleUI ui = new ConsoleUI(helper);

        ui.register("client-add", "Добавить клиента",
                new AddClientCommand(clientService, helper));
        ui.register("client-list", "Показать клиентов",
                new ShowClientsCommand(clientService, helper));
        ui.register("order-create", "Создать заявку",
                new CreateOrderCommand(repairOrderService, helper));
        ui.register("order-list", "Показать заявки",
                new ShowOrdersCommand(repairOrderService, helper));
        ui.register("order-status", "Поменять статус заявки",
                new ChangeOrderStatusCommand(repairOrderService, helper));
        ui.register("order-update", "Редактировать новую заявку",
                new UpdateOrderCommand(repairOrderService, helper));
        ui.register("order-delete", "Удалить новую заявку",
                new DeleteOrderCommand(repairOrderService, helper));
        ui.register("order-find", "Найти заявки по статусу",
                new FindOrdersByStatusCommand(repairOrderService, helper));
        return ui;
    }
}
