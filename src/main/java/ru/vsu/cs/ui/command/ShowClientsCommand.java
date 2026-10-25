package ru.vsu.cs.ui.command;

import ru.vsu.cs.service.ClientService;
import ru.vsu.cs.ui.ConsoleHelper;

public class ShowClientsCommand implements Command {
    private final ClientService service;
    private final ConsoleHelper helper;

    public ShowClientsCommand(ClientService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        helper.printClients(service.findAllClients());
    }
}
