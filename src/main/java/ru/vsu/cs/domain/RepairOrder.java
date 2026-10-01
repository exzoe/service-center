package ru.vsu.cs.domain;

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
        validateDetails(device, problemDescription);
        this.device = device;
        this.problemDescription = problemDescription;
    }

    private void validateDetails(String device, String problemDescription) {
        if(device == null || device.isBlank()) {
            throw new IllegalArgumentException("Название устройства не должно быть пустым");
        }
        if(problemDescription == null || problemDescription.isBlank()) {
            throw new IllegalArgumentException("Описание неисправности не должно быть пустым");
        }
    }

    public void changeStatus(RepairStatus newStatus) {
        if(newStatus == null) {
            throw new IllegalArgumentException("Новый статус не должен быть null");
        }

        switch(status) {
            case NEW:
                if(newStatus == RepairStatus.DIAGNOSTICS || newStatus == RepairStatus.CANCELLED) {
                    this.status = newStatus;
                }
                else{
                    throw new IllegalStateException("Недопустимый переход: " + status + " -> " + newStatus);
                }
                break;
            case DIAGNOSTICS:
                if(newStatus == RepairStatus.IN_REPAIR || newStatus == RepairStatus.CANCELLED) {
                    this.status = newStatus;
                }
                else{
                    throw new IllegalStateException("Недопустимый переход: " + status + " -> " + newStatus);
                }
                break;
            case IN_REPAIR:
                if(newStatus == RepairStatus.READY) {
                    this.status = newStatus;
                }
                else{
                    throw new IllegalStateException("Недопустимый переход: " + status + " -> " + newStatus);
                }
                break;
            case READY:
                if(newStatus == RepairStatus.CLOSED) {
                    this.status = newStatus;
                }
                else{
                    throw new IllegalStateException("Недопустимый переход: " + status + " -> " + newStatus);
                }
                break;
            case CLOSED:
            case CANCELLED:
                throw new IllegalStateException("Нельзя изменить статус завершённой заявки");
            default:
                throw new IllegalArgumentException();
        }
    }

    public void updateDetails(String device, String problemDescription) {
        if(this.status != RepairStatus.NEW) {
            throw new IllegalStateException("Менять описание можно только у новых заявок");
        }
        validateDetails(device, problemDescription);

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
