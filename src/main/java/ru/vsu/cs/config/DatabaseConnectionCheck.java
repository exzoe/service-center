package ru.vsu.cs.config;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.jdbc.JdbcClientRepository;

import java.util.List;

public class DatabaseConnectionCheck {
    public static void main(String[] args) {
        ConnectionProvider provider = new ConnectionProvider();
        JdbcClientRepository repository = new JdbcClientRepository(provider);

        List<Client> clients = repository.findAll();

        for (Client client : clients) {
            System.out.println(
                    client.getId() + ": "
                            + client.getName() + ", "
                            + client.getPhone()
            );
        }

        System.out.println("Всего клиентов: " + clients.size());
    }
}