package ru.vsu.cs.service;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;

import java.util.ArrayList;
import java.util.List;

public class RepairOrderService {
    private final ClientRepository clientRepository;
    private final RepairOrderRepository repairOrderRepository;
    private long nextId = 1;

    public RepairOrderService(ClientRepository clientRepository, RepairOrderRepository repairOrderRepository) {
        this.clientRepository = clientRepository;
        this.repairOrderRepository = repairOrderRepository;
    }

    private RepairOrder orderFindById(long orderId){
        return repairOrderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Заявка не найдена: ID " + orderId));
    }

    public RepairOrder createOrder(long clientId, String device, String problemDescription){
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Клиент не найден: ID " + clientId));

        RepairOrder order = new RepairOrder(nextId, client, device, problemDescription);
        repairOrderRepository.save(order);
        nextId++;

        return order;
    }

    public RepairOrder changeOrderStatus(long orderId, RepairStatus newStatus){
        RepairOrder order = orderFindById(orderId);
        order.changeStatus(newStatus);
        repairOrderRepository.save(order);
        return order;
    }

    public List<RepairOrder> findAllOrders(){
        return repairOrderRepository.findAll();
    }

    public List<RepairOrder> findOrdersByStatus(RepairStatus status){
        if(status == null){
            throw new IllegalArgumentException("Статус для поиска не должен быть null");
        }

        List<RepairOrder> orders = new ArrayList<>();
        for(RepairOrder order : repairOrderRepository.findAll()){
            if(order.getStatus() == status){
                orders.add(order);
            }
        }
        return orders;
    }

    public void deleteOrder(long orderId){
        RepairOrder order = orderFindById(orderId);
        if(order.getStatus() != RepairStatus.NEW){
            throw new IllegalStateException("Удалить можно только новую заявку");
        }
        repairOrderRepository.deleteById(orderId);
    }

    public RepairOrder updateOrder(long orderId, String device, String problemDescription){
        RepairOrder order = orderFindById(orderId);
        order.updateDetails(device, problemDescription);
        repairOrderRepository.save(order);
        return order;
    }
}
