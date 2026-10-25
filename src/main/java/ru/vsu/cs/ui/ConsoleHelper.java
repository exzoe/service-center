package ru.vsu.cs.ui;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;

import java.util.List;
import java.util.Scanner;

public class ConsoleHelper {
    private final Scanner scanner;

    public ConsoleHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public String readLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    public long readId(String prompt) {
        return Long.parseLong(readLine(prompt).trim());
    }

    public RepairStatus readStatus() {
        RepairStatus[] statuses = RepairStatus.values();
        System.out.println("Выберите номер статуса:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.VALIDATION,
                    "Номер статуса должен быть целым числом от 1 до " + statuses.length);
        }
        if (choice < 1 || choice > statuses.length) {
            throw new AppException(ErrorCode.VALIDATION,
                    "Выберите номер статуса от 1 до " + statuses.length);
        }
        return statuses[choice - 1];
    }

    public void printClients(List<Client> clients) {
        if (clients.isEmpty()) {
            System.out.println("Клиентов нет");
            return;
        }
        for (Client client : clients) {
            System.out.println("Клиент " + client.getId() + ": " + client.getName()
                    + ", " + client.getPhone());
        }
    }

    public void printOrders(List<RepairOrder> orders) {
        if (orders.isEmpty()) {
            System.out.println("Заявок нет");
            return;
        }
        for (RepairOrder order : orders) {
            System.out.println("Заявка " + order.getId() + ": " + order.getClient().getName()
                    + ", " + order.getDevice() + ", " + order.getProblemDescription()
                    + ", статус - " + order.getStatus());
        }
    }
}
