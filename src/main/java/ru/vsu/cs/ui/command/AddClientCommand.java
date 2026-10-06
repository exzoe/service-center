package ru.vsu.cs.ui.command;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.ui.ConsoleHelper;

public class AddClientCommand implements Command {
    private final ClientService service;
    private final ConsoleHelper helper;

    public AddClientCommand(ClientService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        String name = helper.readLine("Имя клиента:");
        String phone = helper.readLine("Телефон клиента:");
        Client client = service.createClient(name, phone);
        System.out.println("Клиент создан! ID клиента: " + client.getId());
    }
}
