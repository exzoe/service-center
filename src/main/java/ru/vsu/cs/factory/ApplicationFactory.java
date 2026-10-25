package ru.vsu.cs.factory;

import ru.vsu.cs.ui.ConsoleUI;

public final class ApplicationFactory {
    private ApplicationFactory() {
    }

    public static ConsoleUI create() {
        ServiceFactory.Services services = ServiceFactory.create();
        return ConsoleUIFactory.create(services.clientService(), services.repairOrderService());
    }
}
