package ru.vsu.cs.repository;

import ru.vsu.cs.domain.RepairOrder;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository {

    void save(RepairOrder order);

    Optional<RepairOrder> findById(long id);

    List<RepairOrder> findAll();

    boolean deleteById(long id);

}
