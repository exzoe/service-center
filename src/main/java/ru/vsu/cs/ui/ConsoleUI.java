package ru.vsu.cs.ui;

import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.ui.command.Command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ConsoleUI {
    private static final String EXIT_COMMAND = "exit";

    private final ConsoleHelper helper;
    private final Map<String, MenuEntry> commands = new LinkedHashMap<>();

    private record MenuEntry(String description, Command command) {
    }

    public ConsoleUI(ConsoleHelper helper) {
        this.helper = helper;
    }

    public void register(String name, String description, Command command) {
        commands.put(name, new MenuEntry(description, command));
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();

            String choice;
            try {
                choice = helper.readLine("Введите команду:").trim();
            } catch (NoSuchElementException e) {
                running = false;
                continue;
            }
            if (EXIT_COMMAND.equals(choice)) {
                running = false;
                continue;
            }

            MenuEntry entry = commands.get(choice);
            if (entry == null) {
                System.out.println("Неизвестная команда");
                continue;
            }

            try {
                entry.command().execute();
            } catch (NumberFormatException e) {
                System.out.println("ID должен быть целым числом в диапазоне long");
            } catch (AppException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Ввод завершён");
                running = false;
            }
        }
    }

    private void printMenu() {
        commands.forEach((name, entry) ->
                System.out.println(name + " — " + entry.description()));
        System.out.println(EXIT_COMMAND + " — Выход");
    }
}
