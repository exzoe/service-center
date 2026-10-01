package ru.vsu.cs.service;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.ClientRepository;

import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;
    private long nextId = 1;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(String name, String phone) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя клиента не должно быть пустым");
        }
        if(phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Телефон клиента не должен быть пустым");
        }
        Client client = new Client(nextId, name, phone);
        clientRepository.save(client);
        nextId++;
        return client;

    }

    public List<Client> findAllClients(){
        return clientRepository.findAll();
    }
}
