package ru.vsu.cs.domain;

public class Client {
    private final long id;
    private String name;
    private String phone;

    public Client(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

}
