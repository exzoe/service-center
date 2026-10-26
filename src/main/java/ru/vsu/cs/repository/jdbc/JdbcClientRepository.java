package ru.vsu.cs.repository.jdbc;

import ru.vsu.cs.config.ConnectionProvider;
import ru.vsu.cs.domain.Client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Optional;

public class JdbcClientRepository {
    private final ConnectionProvider connectionProvider;

    public JdbcClientRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

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
}