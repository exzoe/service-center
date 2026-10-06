package ru.vsu.cs;

import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;
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

public final class ApplicationFactory {
    private ApplicationFactory() {
    }

    public static ConsoleUI create() {
        InMemoryClientRepository clientRepository = new InMemoryClientRepository();
        InMemoryRepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
        ClientService clientService = new ClientService(clientRepository);
        RepairOrderService repairOrderService =
                new RepairOrderService(clientRepository, repairOrderRepository);
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
