package ru.vsu.cs.domain;

public class RepairOrder {
    private final Long id;
    private Client client;
    private String device;
    private String problemDescription;
    private RepairStatus status;

    public RepairOrder(Long id, Client client, String device, String problemDescription) {
        this.id = id;
        this.client = client;
        this.status = RepairStatus.NEW;
        this.device = device;
        this.problemDescription = problemDescription;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public void updateDetails(String device, String problemDescription) {
        this.device = device;
        this.problemDescription = problemDescription;
    }

    public Long getId() {
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
