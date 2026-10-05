package ru.vsu.cs.ui;

import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.command.Command;
import ru.vsu.cs.ui.command.AddClientCommand;
import ru.vsu.cs.ui.command.ShowClientsCommand;
import ru.vsu.cs.ui.command.CreateOrderCommand;
import ru.vsu.cs.ui.command.ShowOrdersCommand;
import ru.vsu.cs.ui.command.ChangeOrderStatusCommand;
import ru.vsu.cs.ui.command.UpdateOrderCommand;
import ru.vsu.cs.ui.command.DeleteOrderCommand;
import ru.vsu.cs.ui.command.FindOrdersByStatusCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleUI {
    private final ConsoleHelper helper;
    private final Map<String, Command> commands = new HashMap<>();

    public ConsoleUI(ClientService clientService, RepairOrderService repairOrderService) {
        helper = new ConsoleHelper(new Scanner(System.in));
        commands.put("1", new AddClientCommand(clientService, helper));
        commands.put("2", new ShowClientsCommand(clientService, helper));
        commands.put("3", new CreateOrderCommand(repairOrderService, helper));
        commands.put("4", new ShowOrdersCommand(repairOrderService, helper));
        commands.put("5", new ChangeOrderStatusCommand(repairOrderService, helper));
        commands.put("6", new UpdateOrderCommand(repairOrderService, helper));
        commands.put("7", new DeleteOrderCommand(repairOrderService, helper));
        commands.put("8", new FindOrdersByStatusCommand(repairOrderService, helper));
    }

    public void run() {
        while (true) {
            printMenu();

            String choice;
            try {
                choice = helper.readLine("Выберите пункт меню:").trim();
            } catch (NoSuchElementException e) {
                return;
            }
            if ("0".equals(choice)) {
                return;
            }

            Command command = commands.get(choice);
            if (command == null) {
                System.out.println("Неизвестная команда");
                continue;
            }

            try {
                command.execute();
            } catch (NumberFormatException e) {
                System.out.println("ID должен быть целым числом в диапазоне long");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Ввод завершён");
                return;
            }
        }
    }

    private void printMenu() {
        System.out.println("1. Добавить клиента");
        System.out.println("2. Показать клиентов");
        System.out.println("3. Создать заявку");
        System.out.println("4. Показать заявки");
        System.out.println("5. Поменять статус заявки");
        System.out.println("6. Редактировать новую заявку");
        System.out.println("7. Удалить новую заявку");
        System.out.println("8. Найти заявки по статусу");
        System.out.println("0. Выход");
    }
}
