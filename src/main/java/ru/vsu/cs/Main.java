package ru.vsu.cs;

import ru.vsu.cs.domain.Client;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сервисный центр");
        Client client1 = new Client(1, "Stas", "89515325423");
        System.out.println(client1.getId());
        System.out.println(client1.getName());
        System.out.println(client1.getPhone());
    }
}