package ru.vsu.cs.repository.memory;

import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.repository.RepairOrderRepository;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.id.IdProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepairOrderRepository implements RepairOrderRepository {
    private final Map<Long, RepairOrder> orders = new HashMap<>();
    private final IdProvider idProvider = new IdProvider();

    @Override
    public RepairOrder create(
            Client client, String device, String problemDescription) {

        RepairOrder order = new RepairOrder(
                idProvider.nextId(), client, device, problemDescription);

        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public void update(RepairOrder order) {
        if (!orders.containsKey(order.getId())) {
            throw new IllegalStateException(
                    "Заявка не найдена: " + order.getId());
        }

        orders.put(order.getId(), order);
    }

    @Override
    public Optional<RepairOrder> findById(long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<RepairOrder> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean deleteById(long id) {
        if (orders.containsKey(id)) {
            orders.remove(id);
            return true;
        }
        return false;
    }
}
