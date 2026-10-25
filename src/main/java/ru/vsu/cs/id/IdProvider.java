package ru.vsu.cs.id;

public final class IdProvider {
    private long nextId = 1;

    public long nextId() {
        return nextId++;
    }
}
