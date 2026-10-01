package ru.vsu.cs.ui;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ClientService clientService;
    private final RepairOrderService repairOrderService;

    public ConsoleUI(ClientService clientService, RepairOrderService repairOrderService) {
        this.clientService = clientService;
        this.repairOrderService = repairOrderService;
    }

    public void run() {
        while (true) {
            System.out.println("1. Добавить клиента");
            System.out.println("2. Показать клиентов");
            System.out.println("3. Создать заявку");
            System.out.println("4. Показать заявки");
            System.out.println("5. Поменять статус заявки");
            System.out.println("6. Редактировать заявку");
            System.out.println("7. Удалить новую заявку");
            System.out.println("8. Найти заявки по статусу");
            System.out.println("0. Выход");
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    addClient();
                    break;
                case "2":
                    showClients();
                    break;
                case "3":
                    addOrder();
                    break;
                case "4":
                    showOrders();
                    break;
                case "5":
                    changeOrderStatus();
                    break;
                case "6":
                    updateOrder();
                    break;
                case "7":
                    deleteOrder();
                    break;
                case "8":
                    showOrdersByStatus();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неизвестная команда");
                    break;
            }
        }
    }

    private void addClient(){
        System.out.println("Имя клиента:");
        String name = scanner.nextLine();
        System.out.println("Телефон клиента");
        String phone = scanner.nextLine();
        try{
            Client client = clientService.createClient(name, phone);
            System.out.println("Клиент создан! ID клиента: " + client.getId());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void showClients(){
        List<Client> clients = clientService.findAllClients();
        if (clients.isEmpty()) {
            System.out.println("Клиентов нет");
            return;
        }
        for (Client client : clients) {
            System.out.println("Клиент " + client.getId()+ ": " + client.getName() + ", " + client.getPhone());
        }
    }

    private void addOrder(){
        try {
            System.out.println("Введите id клиента: ");
            long clientId = Long.parseLong(scanner.nextLine().trim());
            System.out.println("Введите название устройства: ");
            String device = scanner.nextLine();
            System.out.println("Введите описание неисправности: ");
            String problemDescription = scanner.nextLine();
            RepairOrder order = repairOrderService.createOrder(clientId, device, problemDescription);
            System.out.println("Заявка успешно создана. ID заявки: " + order.getId() + ", статус - " + order.getStatus());
        }
        catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом в диапазоне long");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showOrders(){
        printOrders(repairOrderService.findAllOrders());
    }

    private void printOrders(List<RepairOrder> orders) {
        if (orders.isEmpty()) {
            System.out.println("Заявок нет");
            return;
        }
        for (RepairOrder order : orders) {
            System.out.println("Заявка " + order.getId() + ": " + order.getClient().getName() +
                    ", " + order.getDevice() + ", " + order.getProblemDescription() +
                    ", статус - " + order.getStatus());
        }
    }

    private void changeOrderStatus(){
        try {
            System.out.println("Введите ID заявки: ");
            long orderId = Long.parseLong(scanner.nextLine().trim());
            RepairStatus newStatus = readStatus();
            repairOrderService.changeOrderStatus(orderId, newStatus);

            System.out.println("Статус заявки изменен. Новый статус - " + newStatus.name());

        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом в диапазоне long");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrder() {
        try {
            System.out.println("Введите ID заявки: ");
            long orderId = Long.parseLong(scanner.nextLine().trim());
            System.out.println("Введите новое название устройства: ");
            String device = scanner.nextLine();
            System.out.println("Введите новое описание неисправности: ");
            String problemDescription = scanner.nextLine();

            RepairOrder order = repairOrderService.updateOrder(orderId, device, problemDescription);
            System.out.println("Заявка обновлена. ID заявки: " + order.getId());
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом в диапазоне long");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteOrder() {
        try {
            System.out.println("Введите ID заявки: ");
            long orderId = Long.parseLong(scanner.nextLine().trim());
            repairOrderService.deleteOrder(orderId);
            System.out.println("Заявка удалена. ID заявки: " + orderId);
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом в диапазоне long");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showOrdersByStatus() {
        try {
            RepairStatus status = readStatus();
            printOrders(repairOrderService.findOrdersByStatus(status));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private RepairStatus readStatus() {
        RepairStatus[] statuses = RepairStatus.values();
        System.out.println("Выберите номер статуса:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Номер статуса должен быть целым числом от 1 до " + statuses.length);
        }
        if (choice < 1 || choice > statuses.length) {
            throw new IllegalArgumentException(
                    "Выберите номер статуса от 1 до " + statuses.length);
        }
        return statuses[choice - 1];
    }
}
