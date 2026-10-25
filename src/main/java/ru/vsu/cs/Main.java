package ru.vsu.cs;

import ru.vsu.cs.factory.ApplicationFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сервисный центр");
        ApplicationFactory.create().run();
    }
}
