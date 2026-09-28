package ru.vsu.cs.repository;

import ru.vsu.cs.domain.Client;

import java.util.Optional;
import java.util.List;

public interface ClientRepository {

    void save(Client client);

    Optional<Client> findById(long id);

    List<Client> findAll();

    boolean deleteById(long id);
}
