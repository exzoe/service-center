package ru.vsu.cs;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.memory.InMemoryClientRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сервисный центр");
        InMemoryClientRepository repository = new InMemoryClientRepository();
        Client client1 = new Client(1, "Stas", "89515325423");
        Client client2 = new Client(2, "Artem", "89525839541");
        Client client3 = new Client(2, "Inna", "89515325423");
        repository.save(client1);
        repository.save(client2);
        System.out.println(repository.findAll().size());
        System.out.println(repository.findById(2));
        System.out.println(repository.findById(3));
        repository.save(client3);
        System.out.println(repository.findAll().size());
        System.out.println(repository.findById(2).orElseThrow().getName());
        System.out.println(repository.findById(2));
        System.out.println(repository.deleteById(1));
        System.out.println(repository.deleteById(1));
    }
}