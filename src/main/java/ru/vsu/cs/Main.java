package ru.vsu.cs;

import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;
import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;
import ru.vsu.cs.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сервисный центр");
        InMemoryClientRepository clientRepository = new InMemoryClientRepository();
        InMemoryRepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
        ClientService clientService = new ClientService(clientRepository);
        RepairOrderService repairOrderService = new RepairOrderService(clientRepository, repairOrderRepository);

        ConsoleUI ui = new ConsoleUI(clientService, repairOrderService);
        ui.run();
    }
}