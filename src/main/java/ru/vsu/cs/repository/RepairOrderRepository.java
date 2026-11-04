package ru.vsu.cs.repository;

import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.Client;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository {
    RepairOrder create(Client client, String device, String problemDescription);

    void update(RepairOrder order);

    Optional<RepairOrder> findById(long id);

    List<RepairOrder> findAll();

    boolean deleteById(long id);
}
