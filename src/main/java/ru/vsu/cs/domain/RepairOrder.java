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
        this.device = device;
        this.problemDescription = problemDescription;
        this.status = RepairStatus.NEW;
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
