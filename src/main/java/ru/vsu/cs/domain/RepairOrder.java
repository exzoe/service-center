package ru.vsu.cs.domain;

import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;
import ru.vsu.cs.validation.InputValidator;

public class RepairOrder {
    private final long id;
    private Client client;
    private String device;
    private String problemDescription;
    private RepairStatus status;

    public RepairOrder(long id, Client client, String device, String problemDescription) {
        this.id = id;
        this.client = client;
        this.status = RepairStatus.NEW;
        InputValidator.validateOrderDetails(device, problemDescription);
        this.device = device;
        this.problemDescription = problemDescription;
    }

    public void setStatus(RepairStatus status) {
        if (status == null) {
            throw new AppException(ErrorCode.VALIDATION, "Новый статус не должен быть null");
        }
        this.status = status;
    }

    public void updateDetails(String device, String problemDescription) {
        InputValidator.validateOrderDetails(device, problemDescription);

        this.device = device;
        this.problemDescription = problemDescription;
    }

    public long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public String getDevice() {
        return device;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public RepairStatus getStatus() {
        return status;
    }
}
