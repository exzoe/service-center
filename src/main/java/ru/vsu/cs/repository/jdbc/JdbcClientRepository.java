package ru.vsu.cs.repository.jdbc;

import ru.vsu.cs.config.ConnectionProvider;
import ru.vsu.cs.domain.Client;
import ru.vsu.cs.repository.ClientRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Optional;

public class JdbcClientRepository implements ClientRepository {
    private final ConnectionProvider connectionProvider;

    public JdbcClientRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, name, phone FROM clients ORDER BY id";

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                Client client = new Client(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getString("phone")
                );
                clients.add(client);
            }

            return clients;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось получить клиентов из базы данных", e);
        }
    }

    @Override
    public Optional<Client> findById(long id) {
        String sql = "SELECT id, name, phone FROM clients WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Client client = new Client(
                            result.getLong("id"),
                            result.getString("name"),
                            result.getString("phone")
                    );
                    return Optional.of(client);
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось найти клиента в базе данных", e);
        }
    }

    @Override
    public Client create(String name, String phone) {
        String sql = """
            INSERT INTO clients (name, phone)
            VALUES (?, ?)
            RETURNING id
            """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, phone);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Client(
                            result.getLong("id"),
                            name,
                            phone
                    );
                }

                throw new IllegalStateException(
                        "База данных не вернула ID клиента");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось создать клиента в базе данных", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось удалить клиента из базы данных", e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = """
            UPDATE clients
            SET name = ?, phone = ?
            WHERE id = ?
            """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, client.getName());
            statement.setString(2, client.getPhone());
            statement.setLong(3, client.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalStateException(
                        "Клиент не найден: " + client.getId());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось обновить клиента в базе данных", e);
        }
    }
}