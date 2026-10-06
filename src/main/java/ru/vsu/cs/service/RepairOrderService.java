package ru.vsu.cs.service;

import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.exception.AppException;
import ru.vsu.cs.exception.ErrorCode;
import ru.vsu.cs.repository.ClientRepository;
import ru.vsu.cs.repository.RepairOrderRepository;

import java.util.ArrayList;
import java.util.List;

public class RepairOrderService {
    private final ClientRepository clientRepository;
    private final RepairOrderRepository repairOrderRepository;
    private long nextId = 1;

    public RepairOrderService(ClientRepository clientRepository,
                              RepairOrderRepository repairOrderRepository) {
        this.clientRepository = clientRepository;
        this.repairOrderRepository = repairOrderRepository;
    }

    private RepairOrder orderFindById(long orderId) {
        return repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.NOT_FOUND, "Заявка не найдена: ID " + orderId));
    }

    public RepairOrder createOrder(long clientId, String device, String problemDescription) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.NOT_FOUND, "Клиент не найден: ID " + clientId));

        RepairOrder order = new RepairOrder(nextId, client, device, problemDescription);
        repairOrderRepository.save(order);
        nextId++;

        return order;
    }

    public RepairOrder changeOrderStatus(long orderId, RepairStatus newStatus) {
        RepairOrder order = orderFindById(orderId);
        checkStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        repairOrderRepository.save(order);
        return order;
    }

    private void checkStatusTransition(RepairStatus currentStatus, RepairStatus newStatus) {
        if (newStatus == null) {
            throw new AppException(ErrorCode.VALIDATION, "Новый статус не должен быть null");
        }
        boolean allowed = switch (currentStatus) {
            case NEW -> newStatus == RepairStatus.DIAGNOSTICS || newStatus == RepairStatus.CANCELLED;
            case DIAGNOSTICS -> newStatus == RepairStatus.IN_REPAIR || newStatus == RepairStatus.CANCELLED;
            case IN_REPAIR -> newStatus == RepairStatus.READY;
            case READY -> newStatus == RepairStatus.CLOSED;
            case CLOSED, CANCELLED -> throw new AppException(
                    ErrorCode.BUSINESS_RULE, "Нельзя изменить статус завершённой заявки");
        };
        if (!allowed) {
            throw new AppException(ErrorCode.BUSINESS_RULE,
                    "Недопустимый переход: " + currentStatus + " -> " + newStatus);
        }
    }

    public List<RepairOrder> findAllOrders() {
        return repairOrderRepository.findAll();
    }

    public List<RepairOrder> findOrdersByStatus(RepairStatus status) {
        if (status == null) {
            throw new AppException(ErrorCode.VALIDATION, "Статус для поиска не должен быть null");
        }

        List<RepairOrder> orders = new ArrayList<>();
        for (RepairOrder order : repairOrderRepository.findAll()) {
            if (order.getStatus() == status) {
                orders.add(order);
            }
        }
        return orders;
    }

    public void deleteOrder(long orderId) {
        RepairOrder order = orderFindById(orderId);
        if (order.getStatus() != RepairStatus.NEW) {
            throw new AppException(ErrorCode.BUSINESS_RULE, "Удалить можно только новую заявку");
        }
        repairOrderRepository.deleteById(orderId);
    }

    public RepairOrder updateOrder(long orderId, String device, String problemDescription) {
        RepairOrder order = orderFindById(orderId);
        if (order.getStatus() != RepairStatus.NEW) {
            throw new AppException(ErrorCode.BUSINESS_RULE, "Менять описание можно только у новых заявок");
        }
        order.updateDetails(device, problemDescription);
        repairOrderRepository.save(order);
        return order;
    }
}
