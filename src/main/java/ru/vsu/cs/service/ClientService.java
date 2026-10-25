package ru.vsu.cs.service;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.id.IdProvider;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.validation.InputValidator;

import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;
    private final IdProvider idProvider;

    public ClientService(ClientRepository clientRepository, IdProvider idProvider) {
        this.clientRepository = clientRepository;
        this.idProvider = idProvider;
    }

    public Client createClient(String name, String phone) {
        InputValidator.validateClient(name, phone);
        Client client = new Client(idProvider.nextId(), name, phone);
        clientRepository.save(client);
        return client;
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }
}
