package ru.vsu.cs.factory;

import ru.vsu.cs.id.IdProvider;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;
import ru.vsu.cs.repository.memory.InMemoryRepairOrderRepository;
import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;

public final class ServiceFactory {
    private ServiceFactory() {
    }

    public record Services(ClientService clientService, RepairOrderService repairOrderService) {
    }

    public static Services create() {
        ClientRepository clientRepository = new InMemoryClientRepository();
        RepairOrderRepository repairOrderRepository = new InMemoryRepairOrderRepository();
        ClientService clientService = new ClientService(clientRepository, new IdProvider());
        RepairOrderService repairOrderService =
                new RepairOrderService(clientRepository, repairOrderRepository, new IdProvider());
        return new Services(clientService, repairOrderService);
    }
}
