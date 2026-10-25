package ru.vsu.cs.validation;

import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;

public final class InputValidator {
    private InputValidator() {
    }

    public static void validateClient(String name, String phone) {
        requireNonBlank(name, "Имя клиента не должно быть пустым");
        requireNonBlank(phone, "Телефон клиента не должен быть пустым");
    }

    public static void validateOrderDetails(String device, String problemDescription) {
        requireNonBlank(device, "Название устройства не должно быть пустым");
        requireNonBlank(problemDescription, "Описание неисправности не должно быть пустым");
    }

    private static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION, message);
        }
    }
}
