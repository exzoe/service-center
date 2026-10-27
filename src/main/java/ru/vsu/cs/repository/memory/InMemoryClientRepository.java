package ru.vsu.cs.repository.memory;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.id.IdProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryClientRepository implements ClientRepository {
    private final Map<Long, Client> clients = new HashMap<>();
    private final IdProvider idProvider = new IdProvider();

    @Override
    public Client create(String name, String phone) {
        Client client = new Client(idProvider.nextId(), name, phone);
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public void update(Client client) {
        if (!clients.containsKey(client.getId())) {
            throw new IllegalStateException(
                    "Клиент не найден: " + client.getId());
        }

        clients.put(client.getId(), client);
    }

    @Override
    public Optional<Client> findById(long id) {
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    @Override
    public boolean deleteById(long id) {
        if (clients.containsKey(id)) {
            clients.remove(id);
            return true;
        }
        return false;
    }
}
