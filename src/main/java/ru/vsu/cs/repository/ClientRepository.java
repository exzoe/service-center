package ru.vsu.cs.repository;

import ru.vsu.cs.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client create(String name, String phone);

    void update(Client client);

    Optional<Client> findById(long id);

    List<Client> findAll();

    boolean deleteById(long id);
}
