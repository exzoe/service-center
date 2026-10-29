package ru.vsu.cs.repository.jdbc;

import ru.vsu.cs.config.ConnectionProvider;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.domain.RepairOrder;
import ru.vsu.cs.domain.RepairStatus;
import ru.vsu.cs.repository.RepairOrderRepository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcRepairOrderRepository implements RepairOrderRepository {
    private final ConnectionProvider connectionProvider;

    public JdbcRepairOrderRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public RepairOrder create(
            Client client, String device, String problemDescription) {

        String sql = """
                INSERT INTO repair_orders
                    (client_id, device, problem_description)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, client.getId());
            statement.setString(2, device);
            statement.setString(3, problemDescription);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new RepairOrder(
                            result.getLong("id"),
                            client,
                            device,
                            problemDescription
                    );
                }

                throw new IllegalStateException(
                        "База данных не вернула ID заявки");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось создать заявку в базе данных", e);
        }
    }

    @Override
    public List<RepairOrder> findAll() {
        List<RepairOrder> orders = new ArrayList<>();

        String sql = """
            SELECT o.id, o.client_id, o.device,
                   o.problem_description, o.status,
                   c.name, c.phone
            FROM repair_orders o
            JOIN clients c ON o.client_id = c.id
            ORDER BY o.id
            """;

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                Client client = new Client(
                        result.getLong("client_id"),
                        result.getString("name"),
                        result.getString("phone")
                );

                RepairOrder order = new RepairOrder(
                        result.getLong("id"),
                        client,
                        result.getString("device"),
                        result.getString("problem_description")
                );

                order.setStatus(
                        RepairStatus.valueOf(result.getString("status"))
                );

                orders.add(order);
            }

            return orders;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось получить заявки из базы данных", e);
        }
    }

    @Override
    public Optional<RepairOrder> findById(long id) {
        String sql = """
            SELECT o.id, o.client_id, o.device,
                   o.problem_description, o.status,
                   c.name, c.phone
            FROM repair_orders o
            JOIN clients c ON o.client_id = c.id
            WHERE o.id = ?
            """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Client client = new Client(
                            result.getLong("client_id"),
                            result.getString("name"),
                            result.getString("phone")
                    );

                    RepairOrder order = new RepairOrder(
                            result.getLong("id"),
                            client,
                            result.getString("device"),
                            result.getString("problem_description")
                    );

                    order.setStatus(RepairStatus.valueOf(result.getString("status")));

                    return Optional.of(order);
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось найти заявку в базе данных", e);
        }
    }

    @Override
    public void update(RepairOrder order) {
        String sql = """
            UPDATE repair_orders
            SET device = ?, problem_description = ?, status = ?
            WHERE id = ?
            """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, order.getDevice());
            statement.setString(2, order.getProblemDescription());
            statement.setString(3, order.getStatus().name());
            statement.setLong(4, order.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalStateException(
                        "Заявка не найдена: " + order.getId());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось обновить заявку в базе данных", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM repair_orders WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось удалить заявку из базы данных", e);
        }
    }
}