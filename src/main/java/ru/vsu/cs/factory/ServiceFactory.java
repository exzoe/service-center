package ru.vsu.cs.factory;

import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;
import ru.vsu.cs.config.ConfigReader;
import ru.vsu.cs.config.ConnectionProvider;
import ru.vsu.cs.config.DatabaseConfig;
import ru.vsu.cs.config.DatabaseMigrator;
import ru.vsu.cs.repository.jdbc.JdbcClientRepository;
import ru.vsu.cs.repository.jdbc.JdbcRepairOrderRepository;
import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.service.RepairOrderService;

public final class ServiceFactory {
    private ServiceFactory() {
    }

    public record Services(ClientService clientService, RepairOrderService repairOrderService) {
    }

    public static Services create() {
        DatabaseConfig config = ConfigReader.readDatabaseConfig();
        DatabaseMigrator.migrate(config);
        ConnectionProvider connectionProvider = new ConnectionProvider(config);
        ClientRepository clientRepository = new JdbcClientRepository(connectionProvider);
        RepairOrderRepository repairOrderRepository = new JdbcRepairOrderRepository(connectionProvider);
        ClientService clientService = new ClientService(clientRepository);
        RepairOrderService repairOrderService = new RepairOrderService(clientRepository, repairOrderRepository);

        return new Services(clientService, repairOrderService);
    }
}
