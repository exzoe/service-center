package ru.vsu.cs.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {
    private final Properties properties = new Properties();

    public ConnectionProvider() {
        try (InputStream input = ConnectionProvider.class
                .getResourceAsStream("/database.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Файл database.properties не найден");
            }

            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Не удалось прочитать настройки базы данных", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}