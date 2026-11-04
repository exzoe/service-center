package ru.vsu.cs.service;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.validation.InputValidator;

import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(String name, String phone) {
        InputValidator.validateClient(name, phone);
        return clientRepository.create(name, phone);
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }
}