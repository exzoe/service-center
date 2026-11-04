package ru.vsu.cs.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private ConfigReader() {
    }

    public static DatabaseConfig readDatabaseConfig() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class
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

        var environment = System.getenv();
        return new DatabaseConfig(
                environment.getOrDefault("DB_URL", properties.getProperty("db.url")),
                environment.getOrDefault("DB_USER", properties.getProperty("db.user")),
                environment.getOrDefault("DB_PASSWORD", properties.getProperty("db.password"))
        );
    }
}
