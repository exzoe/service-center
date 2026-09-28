package ru.vsu.cs;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сервисный центр");
        Client client1 = new Client(1, "Stas", "89515325423");
        RepairOrder order = new RepairOrder(1, client1, "iphone 16", "Не работает");
        System.out.println(order.getId());
        System.out.println(order.getClient().getName());
        System.out.println(order.getClient().getPhone());
        System.out.println(order.getDevice());
        System.out.println(order.getStatus());
    }
}