package ru.vsu.cs.config;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.jdbc.JdbcClientRepository;

import java.util.List;

public class DatabaseConnectionCheck {
    public static void main(String[] args) {
        ConnectionProvider provider = new ConnectionProvider();
        JdbcClientRepository repository = new JdbcClientRepository(provider);
        Client createdClient = repository.create("Иван", "79991234567");
        System.out.println("Создан клиент с ID: " + createdClient.getId());

        Client updatedClient = new Client(
                createdClient.getId(),
                "Иван Петров",
                "79990000000"
        );

        repository.update(updatedClient);

        Client found = repository.findById(updatedClient.getId()).orElseThrow();
        System.out.println(found.getName() + ", " + found.getPhone());

        boolean deleted = repository.deleteById(createdClient.getId());
        System.out.println("Клиент удалён: " + deleted);

        boolean missing = repository.findById(createdClient.getId()).isEmpty();
        System.out.println("Клиент отсутствует в базе: " + missing);

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