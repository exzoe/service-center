package ru.vsu.cs.repository.memory;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.ClientRepository;

import java.util.*;

public class InMemoryClientRepository implements ClientRepository {

    private final Map<Long, Client> clients = new HashMap<>();

    @Override
    public void save(Client client) {
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
        if(clients.containsKey(id)) {
            clients.remove(id);
            return true;
        }
        return false;
    }
}
